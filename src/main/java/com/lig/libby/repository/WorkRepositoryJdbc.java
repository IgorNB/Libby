package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.QWork;
import com.lig.libby.domain.User;
import com.lig.libby.domain.Work;
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
public class WorkRepositoryJdbc extends GenericRepositoryJdbc<Work, QWork> implements WorkRepository {
    public static final ImmutableList<ValueFieldMeta<Work, QWork, ?>> workValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Work, QWork, ?, ?>> workEntityFieldsMeta;
    public static final String WORK_SELECT_ALL_COLUMNS_SQL;

    static {
        List<ValueFieldMeta<Work, QWork, ?>> workMetha = new ArrayList<>();
        workMetha.add(new ValueFieldMeta<>(QWork.work.id, Work.ID_COLUMN, Work::getId, Work::setId, String.class));
        workMetha.add(new ValueFieldMeta<>(QWork.work.version, Work.VERSION_COLUMN, Work::getVersion, Work::setVersion, Integer.class));
        workMetha.add(new ValueFieldMeta<>(QWork.work.updatedDate, Work.UPDATED_DATE_COLUMN, Work::getUpdatedDate, Work::setUpdatedDate, Long.class));
        workMetha.add(new ValueFieldMeta<>(QWork.work.createdDate, Work.CREATED_DATE_COLUMN, Work::getCreatedDate, Work::setCreatedDate, Long.class));
        workValueFieldsMeta = ImmutableList.copyOf(workMetha);
    }


    static {
        List<EntityFieldMeta<Work, QWork, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QWork.work.createdBy, Work.CREATED_BY_COLUMN, Work::getCreatedBy, Work::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QWork.work.lastUpdBy, Work.LAST_UPD_BY_COLUMN, Work::getLastUpdBy, Work::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        workEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        WORK_SELECT_ALL_COLUMNS_SQL = "select " +
                workValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                workEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Work.TABLE + " " + ValueFieldMeta.aliasQ(QWork.work) +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QWork.work.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QWork.work) + "." + Work.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QWork.work.createdBy) + "." + User.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QWork.work.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QWork.work) + "." + Work.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QWork.work.lastUpdBy) + "." + User.ID_COLUMN;

    }

    @Autowired
    public WorkRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(workValueFieldsMeta, workEntityFieldsMeta, WORK_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }


    @Override
    public String getTable() {
        return Work.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Work.ID_COLUMN;
    }

    @Override
    public Supplier<Work> getEntityFactory() {
        return Work::new;
    }
}
