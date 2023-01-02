package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import com.lig.libby.domain.User;
import com.lig.libby.repository.core.jdbc.EntityFieldMeta;
import com.lig.libby.repository.core.jdbc.GenericRepositoryJdbc;
import com.lig.libby.repository.core.jdbc.ValueFieldMeta;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ThreadSafe
@Profile("springJdbc")
@Repository
public class LangRepositoryJdbc extends GenericRepositoryJdbc<Lang, QLang> implements LangRepository {
    public static final ImmutableList<ValueFieldMeta<Lang, QLang, ?>> langValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Lang, QLang, ?, ?>> langEntityFieldsMeta;
    public static final String LANG_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<Lang, QLang, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QLang.lang.id, Lang.ID_COLUMN, Lang::getId, Lang::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QLang.lang.version, Lang.VERSION_COLUMN, Lang::getVersion, Lang::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QLang.lang.updatedDate, Lang.UPDATED_DATE_COLUMN, Lang::getUpdatedDate, Lang::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QLang.lang.createdDate, Lang.CREATED_DATE_COLUMN, Lang::getCreatedDate, Lang::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QLang.lang.code, Lang.NAME_COLUMN, Lang::getCode, Lang::setCode, String.class));

        langValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        final List<EntityFieldMeta<Lang, QLang, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QLang.lang.createdBy, Lang.CREATED_BY_COLUMN, Lang::getCreatedBy, Lang::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QLang.lang.lastUpdBy, Lang.LAST_UPD_BY_COLUMN, Lang::getLastUpdBy, Lang::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        langEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        LANG_SELECT_ALL_COLUMNS_SQL = "select " +
                langValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                langEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Lang.TABLE + " " + ValueFieldMeta.aliasQ(QLang.lang) +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QLang.lang.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QLang.lang) + "." + Lang.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QLang.lang.createdBy) + "." + User.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QLang.lang.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QLang.lang) + "." + Lang.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QLang.lang.lastUpdBy) + "." + User.ID_COLUMN;

    }

    @Autowired
    public LangRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(langValueFieldsMeta, langEntityFieldsMeta, LANG_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }


    @Override
    public String getTable() {
        return Lang.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Lang.ID_COLUMN;
    }

    @Override
    public Supplier<Lang> getEntityFactory() {
        return Lang::new;
    }
}
