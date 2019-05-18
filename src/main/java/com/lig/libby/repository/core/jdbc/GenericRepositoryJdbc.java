package com.lig.libby.repository.core.jdbc;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.core.AbstractPersistentObject;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import com.lig.libby.domain.core.PersistentObject;
import com.lig.libby.repository.core.GenericAllMethodsNotSupportedRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ThreadSafe
@Profile("springJdbc")
@SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
public abstract class GenericRepositoryJdbc<E extends PersistentObject, Q extends EntityPathBase<E>> extends GenericAllMethodsNotSupportedRepository<E, Q, String> {

    @NonNull
    public final ImmutableList<ValueFieldMeta<E, Q, ?>> valueFieldsMeta;
    @NonNull
    public final ImmutableList<EntityFieldMeta<E, Q, ?, ?>> entityFieldsMeta;
    @NonNull
    public final String selectAllColumnsSql;
    @NonNull
    public final RowMapper<E> entityMapper;
    @NonNull
    public final NamedParameterJdbcOperations jdbcOp;

    public GenericRepositoryJdbc(@NonNull ImmutableList<ValueFieldMeta<E, Q, ?>> valueFieldsMeta, @NonNull ImmutableList<EntityFieldMeta<E, Q, ?, ?>> entityFieldsMeta, @NonNull String selectAllColumnsSql, @NonNull NamedParameterJdbcOperations jdbcOp) {
        this.valueFieldsMeta = valueFieldsMeta;
        this.entityFieldsMeta = entityFieldsMeta;
        this.selectAllColumnsSql = selectAllColumnsSql;
        this.jdbcOp = jdbcOp;
        this.entityMapper = new EntityMapper();
    }

    public static Long getNullableLong(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel) == null ? null : resultSet.getLong(columnLabel);
    }

    public static Integer getNullableInt(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel) == null ? null : resultSet.getInt(columnLabel);
    }

    public static BigInteger getNullableBigInteger(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel) == null ? null : resultSet.getBigDecimal(columnLabel).toBigInteger();
    }

    public static Float getNullableFloat(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel) == null ? null : resultSet.getFloat(columnLabel);
    }

    public static Boolean getNullableBoolean(ResultSet resultSet, String columnLabel) throws SQLException {
        return resultSet.getString(columnLabel) == null ? null : resultSet.getBoolean(columnLabel);
    }

    public abstract Supplier<E> getEntityFactory();

    public abstract String getTable();

    public abstract String getIdColumn();

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public <S extends E> S saveAndFlush(S entity) {
        return this.save(entity);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public <S extends E> S save(S entity) {
        List<ValueFieldMeta<E, Q, ?>> updatableValueFields = valueFieldsMeta.stream().filter(f -> !f.getFieldAlias().getAnnotatedElement().isAnnotationPresent(Formula.class)).collect(Collectors.toList());
        final Map<String, Object> params2 = new HashMap<>(1);
        Optional<E> entityCurrent = findById(entity.getId());
        updatableValueFields.forEach(
                field -> {
                    if (AbstractPersistentObject.VERSION_COLUMN.equals(field.getFieldColumn())) {
                        entity.setVersion(Optional.ofNullable(entity.getVersion()).map(v -> (entityCurrent.isPresent() ? v + 1 : 0)).orElse(0));
                        params2.put(AbstractPersistentObject.VERSION_COLUMN, entity.getVersion());
                    } else if (field.getFieldClass().isEnum()) {
                        Object fieldValue = field.getFieldGetter().apply(entity);
                        Enum typedFieldValue = (Enum) fieldValue;
                        params2.put(field.getFieldColumn(), typedFieldValue.name());
                    } else {
                        Object fieldValue = field.getFieldGetter().apply(entity);
                        params2.put(field.getFieldColumn(), fieldValue);
                    }
                });

        entityFieldsMeta.forEach(field -> {
            PersistentObject childFieldValue = field.getFieldGetter().apply(entity);
            String childFieldIdString = Optional.ofNullable(childFieldValue).map(PersistentObject::getId).orElse(null);
            if (GenericAbstractPersistentAuditingObject.CREATED_BY_COLUMN.equals(field.getFieldColumn()) && !entityCurrent.isPresent()) {
                params2.put(field.getFieldColumn(), getAuthUserId().orElse(childFieldIdString));
            } else if (GenericAbstractPersistentAuditingObject.LAST_UPD_BY_COLUMN.equals(field.getFieldColumn())) {
                params2.put(field.getFieldColumn(), getAuthUserId().orElse(childFieldIdString));
            } else {
                params2.put(field.getFieldColumn(), childFieldIdString);
            }
        });

        if (entityCurrent.isPresent()) {
            jdbcOp.update("UPDATE " + getTable() +
                            " set" +
                            updatableValueFields.stream().map(f -> " " + f.getFieldColumn() + " =:" + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " ," +
                            entityFieldsMeta.stream().map(f -> " " + f.getFieldColumn() + " =:" + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " where " + getIdColumn() + " = :" + getIdColumn()
                    , params2);
        } else {
            jdbcOp.update("INSERT INTO " + getTable() +
                            " (" +
                            updatableValueFields.stream().map(f -> " " + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " ," +
                            entityFieldsMeta.stream().map(f -> " " + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " ) " +
                            " values" +
                            " (" +
                            updatableValueFields.stream().map(f -> " :" + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " ," +
                            entityFieldsMeta.stream().map(f -> " :" + f.getFieldColumn()).collect(Collectors.joining(",")) +
                            " )"
                    , params2);
        }
        return (S) findById(entity.getId()).orElse(null); //TODO>>dirty>>refactor
    }

    private Optional<String> getAuthUserId() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .map(authentication -> {
                    if (authentication.getPrincipal() instanceof UserDetails) {
                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                        return userDetails.getUsername();
                    }
                    return null;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<E> findAll(Predicate predicate) {
        HashMap<String, Object> constantsBinding = new HashMap<>();
        String predicateString = predicate.accept(new QueryDslToStringVisitor(constantsBinding), QueryDslTemplates.DEFAULT);
        String predicateWhereClause = (("".equals(predicateString)) ? "" : " where " + predicateString + " ");

        return jdbcOp.query("select * from (" + selectAllColumnsSql + ") " + predicateWhereClause
                , constantsBinding
                , entityMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<E> findAll(Predicate predicate, Pageable pageable) {
        Assert.notNull(pageable, "Pageable must not be null!");

        HashMap<String, Object> constantsBinding = new HashMap<>();
        String predicateString = predicate != null ? predicate.accept(new QueryDslToStringVisitor(constantsBinding), QueryDslTemplates.DEFAULT) : null;
        String predicateWhereClause = (("".equals(predicateString)) || predicateString == null ? "" : " where " + predicateString + " ");

        String parentAlias = this.getIdFieldMetha().getFieldParentAlias();
        String pageableSortString = pageable.getSort().stream()
                .map(s -> " " + parentAlias + "_" + s.getProperty().replace(".", "_") + " " + s.getDirection() + " ")
                .collect(Collectors.joining(",", "", ""));

        String pageableSortClause = ("".equals(pageableSortString)) || pageableSortString == null ? "" : " ORDER BY " + pageableSortString + " ";
        constantsBinding.put("offset", pageable.getOffset());
        constantsBinding.put("pageSize", pageable.getPageSize());

        List<E> query = jdbcOp.query(" select * from (" + selectAllColumnsSql + ") " + predicateWhereClause + pageableSortClause +
                        " OFFSET :offset ROWS " +
                        " FETCH NEXT :pageSize ROWS ONLY "
                , constantsBinding
                , entityMapper);


        LongSupplier countSupplier = () -> {
            List<Long> count = jdbcOp.query(" select count(1) as count from (select *  from (" + selectAllColumnsSql + ") " + predicateWhereClause + ")"
                    , constantsBinding
                    , (resultSet, i) -> resultSet.getLong("count"));
            return count.stream().findFirst().orElse(0L);
        };

        return PageableExecutionUtils.getPage(query, pageable, countSupplier);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public Optional<E> findById(@NonNull String s) {
        final Map<String, Object> params = new HashMap<>(1);

        ValueFieldMeta<E, Q, ?> idFieldMetha = getIdFieldMetha();

        params.put(idFieldMetha.getFieldAliasString(), s);
        return jdbcOp.query(selectAllColumnsSql +
                        " where " + idFieldMetha.getFieldParentAlias() + "." + idFieldMetha.getFieldColumn() + " = :" + idFieldMetha.getFieldAliasString()
                , params
                , entityMapper)
                .stream()
                .findFirst();
    }

    private ValueFieldMeta<E, Q, ?> getIdFieldMetha() {
        return valueFieldsMeta
                .stream()
                .filter(f -> f.getFieldColumn().equals(getIdColumn()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    @Transactional
    public void deleteAll() {
        jdbcOp.update(" DELETE FROM " + getTable()
                , new HashMap<String, Object>()
        );
    }

    @Transactional
    @Override
    public void deleteAll(Iterable<? extends E> entities) {
        entities.forEach(e -> this.deleteById(e.getId()));
    }

    @Override
    @Transactional
    public void deleteById(@NonNull String s) {
        final Map<String, Object> params = new HashMap<>(1);

        ValueFieldMeta<E, Q, ?> idFieldMetha = valueFieldsMeta
                .stream()
                .filter(f -> f.getFieldColumn().equals(getIdColumn()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        params.put(idFieldMetha.getFieldAliasString(), s);

        jdbcOp.update(" DELETE FROM " + getTable() + " " + idFieldMetha.getFieldParentAlias() +
                        " WHERE " + idFieldMetha.getFieldParentAlias() + "." + idFieldMetha.getFieldColumn() + " = :" + idFieldMetha.getFieldAliasString()
                , params);
    }

    public <T extends Object> void readValueFieldMapper(@NonNull ResultSet resultSet, T entity, ValueFieldMeta<?, ?, ?> field, String fieldAlias) {
        try {
            fieldAlias = (fieldAlias != null ? fieldAlias : "");
            @NonNull BiConsumer<T, Object> typedSetter = (BiConsumer<T, Object>) field.getFieldSetter();
            @NonNull Class<?> clazz = field.getFieldClass();

            if (clazz.isAssignableFrom(String.class)) {
                typedSetter.accept(entity, resultSet.getString(fieldAlias));
            } else if (clazz.isAssignableFrom(Integer.class)) {
                typedSetter.accept(entity, getNullableInt(resultSet, fieldAlias));
            } else if (clazz.isAssignableFrom(Long.class)) {
                typedSetter.accept(entity, getNullableLong(resultSet, fieldAlias));
            } else if (clazz.isAssignableFrom(BigInteger.class)) {
                typedSetter.accept(entity, getNullableBigInteger(resultSet, fieldAlias));
            } else if (clazz.isAssignableFrom(Float.class)) {
                typedSetter.accept(entity, getNullableFloat(resultSet, fieldAlias));
            } else if (clazz.isAssignableFrom(Boolean.class)) {
                typedSetter.accept(entity, getNullableBoolean(resultSet, fieldAlias));
            } else if (clazz.isEnum()) {
                if (resultSet.getString(fieldAlias) == null) {
                    typedSetter.accept(entity, null);
                } else {
                    typedSetter.accept(entity, Enum.valueOf((Class<Enum>) clazz, resultSet.getString(fieldAlias)));
                }

            } else {
                throw (new RuntimeException("Type " + field.getFieldClass() + " is not supported in jdbc read mapper"));
            }
        } catch (SQLException e) {
            throw (new RuntimeException(e));
        }
    }

    public <E> void acceptObject(BiConsumer<E, ?> biConsumer, E entity, Object argument) {
        BiConsumer<E, Object> typedBiConsumer = (BiConsumer<E, Object>) biConsumer;
        typedBiConsumer.accept(entity, argument);
    }

    public class EntityMapper implements RowMapper<E> {
        @Override
        public E mapRow(@NonNull ResultSet resultSet, int i) throws SQLException {

            E entity = getEntityFactory().get();

            valueFieldsMeta
                    .forEach(
                            valueField ->
                                    readValueFieldMapper(resultSet, entity, valueField, valueField.getFieldAliasString())
                    );

            entityFieldsMeta
                    .forEach(
                            persistentObjectField -> {
                                ValueFieldMeta<?, ?, ?> idFieldMetha = persistentObjectField.getChildMetha()
                                        .stream()
                                        .filter(f -> f.getFieldColumn().equals(AbstractPersistentObject.ID_COLUMN))//TODO>>IBORISENKO>>fix, that this will not work for PresistentObject not extended from AbstractPersistentObject
                                        .findFirst()
                                        .orElseThrow(IllegalArgumentException::new);
                                try {
                                    if (resultSet.getString(persistentObjectField.getFieldParentAlias() + "_" + persistentObjectField.getFieldName() + "_" + idFieldMetha.getFieldName()) != null) {
                                        Object childEntity = persistentObjectField.getFieldClassFactory().get();
                                        persistentObjectField.getChildMetha().forEach(valueField -> {
                                                    readValueFieldMapper(resultSet, childEntity, valueField, persistentObjectField.getFieldParentAlias() + "_" + persistentObjectField.getFieldName() + "_" + valueField.getFieldName());
                                                    acceptObject(persistentObjectField.getFieldSetter(), entity, childEntity);
                                                }
                                        );
                                    }
                                } catch (SQLException e) {
                                    throw (new RuntimeException("Error retrieving field" + idFieldMetha.getFieldAliasString()));
                                }

                            });
            return entity;
        }
    }

}
