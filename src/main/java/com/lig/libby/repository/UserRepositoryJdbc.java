package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.lig.libby.repository.core.jdbc.EntityFieldMeta;
import com.lig.libby.repository.core.jdbc.GenericRepositoryJdbc;
import com.lig.libby.repository.core.jdbc.ValueFieldMeta;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ThreadSafe
@Profile("springJdbc")
@Repository
@SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
public class UserRepositoryJdbc extends GenericRepositoryJdbc<User, QUser> implements UserRepository {
    public static final ImmutableList<ValueFieldMeta<User, QUser, ?>> userValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<User, QUser, ?, ?>> userEntityFieldsMeta;
    public static final String USER_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<User, QUser, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QUser.user.id, User.ID_COLUMN, User::getId, User::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.version, User.VERSION_COLUMN, User::getVersion, User::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.createdDate, User.CREATED_DATE_COLUMN, User::getCreatedDate, User::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.updatedDate, User.UPDATED_DATE_COLUMN, User::getUpdatedDate, User::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.name, User.Columns.NAME_COLUMN, User::getName, User::setName, String.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.email, User.Columns.EMAIL_COLUMN, User::getEmail, User::setEmail, String.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.password, User.Columns.PASSWORD_COLUMN, User::getPassword, User::setPassword, String.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.emailVerified, User.Columns.EMAIL_VERIFIED_COLUMN, User::getEmailVerified, User::setEmailVerified, Boolean.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.provider, User.Columns.PROVIDER_COLUMN, User::getProvider, User::setProvider, Authority.AuthProvider.class));
        tmp.add(new ValueFieldMeta<>(QUser.user.imageUrl, User.Columns.IMAGE_URL_COLUMN, User::getImageUrl, User::setImageUrl, String.class));

        userValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        List<EntityFieldMeta<User, QUser, ?, ?>> tmp = new ArrayList<>();

        tmp.add(new EntityFieldMeta<>(QUser.user.createdBy, User.CREATED_BY_COLUMN, User::getCreatedBy, User::setCreatedBy, userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QUser.user.lastUpdBy, User.LAST_UPD_BY_COLUMN, User::getLastUpdBy, User::setLastUpdBy, userValueFieldsMeta, User.class, User::new));
        userEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        USER_SELECT_ALL_COLUMNS_SQL = "select " +
                userValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                userEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + User.TABLE + " " + ValueFieldMeta.aliasQ(QUser.user) +

                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QUser.user.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QUser.user) + "." + User.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QUser.user.createdBy) + "." + User.ID_COLUMN +

                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QUser.user.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QUser.user) + "." + User.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QUser.user.lastUpdBy) + "." + User.ID_COLUMN;

    }

    @Autowired
    public UserRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(userValueFieldsMeta, userEntityFieldsMeta, USER_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }


    @Override
    public String getTable() {
        return User.TABLE;
    }

    @Override
    public String getIdColumn() {
        return User.ID_COLUMN;
    }

    @Override
    public Supplier<User> getEntityFactory() {
        return User::new;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(QUser.user.email.eq(email));
        Iterator<User> iterator = findAll(where).iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }

        User user = iterator.next();
        List<Authority> userAuthorities = findManyToManyAuthoritiesByUser(user);
        user.setAuthorities(new HashSet<>(userAuthorities));
        return Optional.ofNullable(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findFirstWithAdminAuthority() {
        HashMap<String, String> params = new HashMap<>();
        params.put("adminAuthority", Authority.Roles.ADMIN);
        String userId = jdbcOp.query(" select " +
                        "  ua." + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN + " as ua_user_id " +
                        " from " + User.USER_AUTHORITY_JOIN_TABLE + " ua" +
                        " left join " + Authority.TABLE + " a " +
                        " on a." + Authority.ID_COLUMN + "= ua." + User.USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN +
                        " where a." + Authority.NAME_COLUMN + "=:adminAuthority"
                , params
                , (resultSet, i) -> resultSet.getString("ua_user_id")).stream().findFirst().orElse(null);
        return userId == null ? null : this.findById(userId).orElse(null);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public <S extends User> S save(S entity) {
        S entitySaved = super.save(entity);
        this.saveManyToManyAuthoritiesByUser(entity);
        entitySaved.setAuthorities(entity.getAuthorities());
        return entitySaved;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public <S extends User> S saveAndFlush(S entity) {
        return this.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(@NonNull String s) {
        Map<String, String> params = new HashMap<>();
        params.put("userId", s);
        jdbcOp.update(" DELETE FROM " + User.USER_AUTHORITY_JOIN_TABLE + " ua " +
                        " WHERE ua." + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN + "=:userId"
                , params);
        super.deleteById(s);
    }

    @NotNull
    private List<Authority> findManyToManyAuthoritiesByUser(User user) {
        return jdbcOp.query(" select " +
                        "  a." + Authority.ID_COLUMN + " as a_id " +
                        ", a." + Authority.VERSION_COLUMN + " as a_version " +
                        ", a." + Authority.NAME_COLUMN + " as a_name " +
                        " from " + User.USER_AUTHORITY_JOIN_TABLE + " ua" +
                        " left join " + Authority.TABLE + " a " +
                        " on a." + Authority.ID_COLUMN + "= ua." + User.USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN +
                        " where ua." + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN + " = \'" + user.getId() + "\'"
                , new HashMap<>()
                , (resultSet, i) -> {
                    Authority authority = new Authority();
                    authority.setId(resultSet.getString("a_id"));
                    authority.setName(resultSet.getString("a_name"));
                    authority.setVersion(GenericRepositoryJdbc.getNullableInt(resultSet, "a_version"));
                    return authority;
                });
    }

    @NotNull
    private void saveManyToManyAuthoritiesByUser(User user) {
        Set<Authority> newAuthorities = user.getAuthorities();
        Set<Authority> currentAuthorities = new HashSet<>(findManyToManyAuthoritiesByUser(user));

        Set<Authority> toInsertAuthorities = newAuthorities.stream()
                .distinct()
                .filter(o -> !currentAuthorities.contains(o))
                .collect(Collectors.toSet());

        if (!toInsertAuthorities.isEmpty()) {
            jdbcOp.update(" INSERT INTO " + User.USER_AUTHORITY_JOIN_TABLE +
                            " ( " + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN +
                            " , " + User.USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN +
                            " ) " +
                            " values " +
                            toInsertAuthorities.stream().map(a -> "(\'" + user.getId() + "\',\'" + a.getId() + "\')").collect(Collectors.joining(","))
                    , new HashMap<String, Object>()
            );
        }

        Set<Authority> toDeleteAuthorities = currentAuthorities.stream()
                .distinct()
                .filter(o -> !currentAuthorities.contains(o))
                .collect(Collectors.toSet());

        if (!toDeleteAuthorities.isEmpty()) {
            jdbcOp.update(" DELETE FROM " + User.USER_AUTHORITY_JOIN_TABLE +
                            " where " + User.USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN + "=\'" + user.getId() + "\'" +
                            " and " + User.USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN + " IN " +
                            "(" +
                            toDeleteAuthorities.stream().map(a -> "\'" + a.getId() + "\'").collect(Collectors.joining(",")) +
                            ")"
                    , new HashMap<String, Object>()
            );
        }
    }

}
