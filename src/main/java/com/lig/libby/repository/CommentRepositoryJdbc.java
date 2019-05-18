package com.lig.libby.repository;

import com.google.common.collect.ImmutableList;
import com.lig.libby.domain.Book;
import com.lig.libby.domain.Comment;
import com.lig.libby.domain.QComment;
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
@SuppressWarnings("squid:S1192") //ignore "String literals should not be duplicated" rule for jdbc class
public class CommentRepositoryJdbc extends GenericRepositoryJdbc<Comment, QComment> implements CommentRepository {
    public static final ImmutableList<ValueFieldMeta<Comment, QComment, ?>> commentValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Comment, QComment, ?, ?>> commentEntityFieldsMeta;
    public static final String LANG_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<Comment, QComment, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QComment.comment.id, Comment.ID_COLUMN, Comment::getId, Comment::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QComment.comment.version, Comment.VERSION_COLUMN, Comment::getVersion, Comment::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QComment.comment.updatedDate, Comment.UPDATED_DATE_COLUMN, Comment::getUpdatedDate, Comment::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QComment.comment.createdDate, Comment.CREATED_DATE_COLUMN, Comment::getCreatedDate, Comment::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QComment.comment.body, Comment.Columns.BODY_COLUMN, Comment::getBody, Comment::setBody, String.class));
        tmp.add(new ValueFieldMeta<>(QComment.comment.rating, Comment.Columns.RATING_COLUMN, Comment::getRating, Comment::setRating, Integer.class));
        commentValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        final List<EntityFieldMeta<Comment, QComment, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QComment.comment.createdBy, Comment.CREATED_BY_COLUMN, Comment::getCreatedBy, Comment::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QComment.comment.lastUpdBy, Comment.LAST_UPD_BY_COLUMN, Comment::getLastUpdBy, Comment::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QComment.comment.book, Comment.Columns.BOOK_ID_COLUMN, Comment::getBook, Comment::setBook, BookRepositoryJdbc.bookValueFieldsMeta, Book.class, Book::new));
        commentEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }


    static {
        LANG_SELECT_ALL_COLUMNS_SQL = "select " +
                commentValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                commentEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Comment.TABLE + " " + ValueFieldMeta.aliasQ(QComment.comment) +
                " left join " + Book.TABLE + " " + ValueFieldMeta.aliasQ(QComment.comment.book) +
                " on " + ValueFieldMeta.aliasQ(QComment.comment) + "." + Comment.Columns.BOOK_ID_COLUMN + " = " + ValueFieldMeta.aliasQ(QComment.comment.book) + "." + Book.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QComment.comment.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QComment.comment) + "." + Comment.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QComment.comment.createdBy) + "." + User.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QComment.comment.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QComment.comment) + "." + Comment.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QComment.comment.lastUpdBy) + "." + User.ID_COLUMN;

    }

    @Autowired
    public CommentRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(commentValueFieldsMeta, commentEntityFieldsMeta, LANG_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }


    @Override
    public String getTable() {
        return Comment.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Comment.ID_COLUMN;
    }

    @Override
    public Supplier<Comment> getEntityFactory() {
        return Comment::new;
    }
}
