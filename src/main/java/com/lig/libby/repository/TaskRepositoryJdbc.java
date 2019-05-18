package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.*;
import com.lig.libby.repository.core.jdbc.EntityFieldMeta;
import com.lig.libby.repository.core.jdbc.GenericRepositoryJdbc;
import com.lig.libby.repository.core.jdbc.ValueFieldMeta;
import lombok.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ThreadSafe
@Profile("springJdbc")
@Repository
@SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
public class TaskRepositoryJdbc extends GenericRepositoryJdbc<Task, QTask> implements TaskRepository {
    public static final ImmutableList<ValueFieldMeta<Task, QTask, ?>> taskValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Task, QTask, ?, ?>> taskEntityFieldsMeta;
    public static final String BOOK_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<Task, QTask, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QTask.task.id, Task.ID_COLUMN, Task::getId, Task::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.version, Task.VERSION_COLUMN, Task::getVersion, Task::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.updatedDate, Task.UPDATED_DATE_COLUMN, Task::getUpdatedDate, Task::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.createdDate, Task.CREATED_DATE_COLUMN, Task::getCreatedDate, Task::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.bookName, Task.Columns.BOOK_NAME, Task::getBookName, Task::setBookName, String.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.bookTitle, Task.Columns.BOOK_TITLE, Task::getBookTitle, Task::setBookTitle, String.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.workflowStep, Task.Columns.WORKFLOW_STEP, Task::getWorkflowStep, Task::setWorkflowStep, Task.WorkflowStepEnum.class));

        tmp.add(new ValueFieldMeta<>(QTask.task.bookIsbn, Task.Columns.BOOK_ISBN, Task::getBookIsbn, Task::setBookIsbn, String.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.bookIsbn13, Task.Columns.BOOK_ISBN_13, Task::getBookIsbn13, Task::setBookIsbn13, BigInteger.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.bookSmallImageUrl, Task.Columns.BOOK_SMALL_IMAGE_URL, Task::getBookSmallImageUrl, Task::setBookSmallImageUrl, String.class));
        tmp.add(new ValueFieldMeta<>(QTask.task.bookAuthors, Task.Columns.BOOK_AUTHORS, Task::getBookAuthors, Task::setBookAuthors, String.class));
        taskValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        final List<EntityFieldMeta<Task, QTask, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QTask.task.createdBy, Task.CREATED_BY_COLUMN, Task::getCreatedBy, Task::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QTask.task.lastUpdBy, Task.LAST_UPD_BY_COLUMN, Task::getLastUpdBy, Task::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QTask.task.bookLang, Task.Columns.BOOK_LANG_ID, Task::getBookLang, Task::setBookLang, LangRepositoryJdbc.langValueFieldsMeta, Lang.class, Lang::new));
        tmp.add(new EntityFieldMeta<>(QTask.task.bookWork, Task.Columns.BOOK_WORK_ID, Task::getBookWork, Task::setBookWork, WorkRepositoryJdbc.workValueFieldsMeta, Work.class, Work::new));
        tmp.add(new EntityFieldMeta<>(QTask.task.assignee, Task.Columns.ASSIGNEE_USER_ID, Task::getAssignee, Task::setAssignee, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QTask.task.book, Task.Columns.BOOK_ID, Task::getBook, Task::setBook, BookRepositoryJdbc.bookValueFieldsMeta, Book.class, Book::new));

        taskEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        BOOK_SELECT_ALL_COLUMNS_SQL = "select " +
                taskValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                taskEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Task.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task) +
                " left join " + Lang.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.bookLang) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.Columns.BOOK_LANG_ID + " = " + ValueFieldMeta.aliasQ(QTask.task.bookLang) + "." + Lang.ID_COLUMN +

                " left join " + Work.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.bookWork) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.Columns.BOOK_WORK_ID + " = " + ValueFieldMeta.aliasQ(QTask.task.bookWork) + "." + Work.ID_COLUMN +

                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QTask.task.createdBy) + "." + User.ID_COLUMN +

                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QTask.task.lastUpdBy) + "." + User.ID_COLUMN +

                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.assignee) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.Columns.ASSIGNEE_USER_ID + " = " + ValueFieldMeta.aliasQ(QTask.task.assignee) + "." + User.ID_COLUMN +
        
                " left join " + Book.TABLE + " " + ValueFieldMeta.aliasQ(QTask.task.book) +
                " on " + ValueFieldMeta.aliasQ(QTask.task) + "." + Task.Columns.BOOK_ID + " = " + ValueFieldMeta.aliasQ(QTask.task.book) + "." + Book.ID_COLUMN;
    }

    @Autowired
    public TaskRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(taskValueFieldsMeta, taskEntityFieldsMeta, BOOK_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }

    @Override
    public String getTable() {
        return Task.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Task.ID_COLUMN;
    }

    @Override
    public Supplier<Task> getEntityFactory() {
        return Task::new;
    }
}
