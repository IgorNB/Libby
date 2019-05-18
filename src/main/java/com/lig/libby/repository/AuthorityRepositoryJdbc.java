package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QAuthority;
import com.lig.libby.domain.User;
import com.lig.libby.repository.core.jdbc.EntityFieldMeta;
import com.lig.libby.repository.core.jdbc.GenericRepositoryJdbc;
import com.lig.libby.repository.core.jdbc.ValueFieldMeta;
import com.querydsl.core.BooleanBuilder;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ThreadSafe
@Profile("springJdbc")
@Repository
public class AuthorityRepositoryJdbc extends GenericRepositoryJdbc<Authority, QAuthority> implements AuthorityRepository {
    public static final ImmutableList<ValueFieldMeta<Authority, QAuthority, ?>> authorityValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Authority, QAuthority, ?, ?>> authorityEntityFieldsMeta;
    public static final String AUTH_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<Authority, QAuthority, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QAuthority.authority.id, Authority.ID_COLUMN, Authority::getId, Authority::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QAuthority.authority.version, Authority.VERSION_COLUMN, Authority::getVersion, Authority::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QAuthority.authority.updatedDate, Authority.UPDATED_DATE_COLUMN, Authority::getUpdatedDate, Authority::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QAuthority.authority.createdDate, Authority.CREATED_DATE_COLUMN, Authority::getCreatedDate, Authority::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QAuthority.authority.name, Authority.NAME_COLUMN, Authority::getName, Authority::setName, String.class));

        authorityValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        final List<EntityFieldMeta<Authority, QAuthority, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QAuthority.authority.createdBy, Authority.CREATED_BY_COLUMN, Authority::getCreatedBy, Authority::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QAuthority.authority.lastUpdBy, Authority.LAST_UPD_BY_COLUMN, Authority::getLastUpdBy, Authority::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        authorityEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        AUTH_SELECT_ALL_COLUMNS_SQL = "select " +
                authorityValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                authorityEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Authority.TABLE + " " + ValueFieldMeta.aliasQ(QAuthority.authority) +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QAuthority.authority.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QAuthority.authority) + "." + Authority.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QAuthority.authority.createdBy) + "." + User.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QAuthority.authority.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QAuthority.authority) + "." + Authority.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QAuthority.authority.lastUpdBy) + "." + User.ID_COLUMN;

    }

    @Autowired
    public AuthorityRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(authorityValueFieldsMeta, authorityEntityFieldsMeta, AUTH_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }


    @Override
    public String getTable() {
        return Authority.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Authority.ID_COLUMN;
    }

    @Override
    public Supplier<Authority> getEntityFactory() {
        return Authority::new;
    }

    @Override
    public Authority getAuthorityByName(@NonNull String name) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(QAuthority.authority.name.eq(name));
        Iterator<Authority> iter = findAll(where).iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }
}
