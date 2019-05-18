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
public class BookRepositoryJdbc extends GenericRepositoryJdbc<Book, QBook> implements BookRepository {
    public static final ImmutableList<ValueFieldMeta<Book, QBook, ?>> bookValueFieldsMeta;
    public static final ImmutableList<EntityFieldMeta<Book, QBook, ?, ?>> bookEntityFieldsMeta;
    public static final String BOOK_SELECT_ALL_COLUMNS_SQL;

    static {
        final List<ValueFieldMeta<Book, QBook, ?>> tmp = new ArrayList<>();
        tmp.add(new ValueFieldMeta<>(QBook.book.id, Book.ID_COLUMN, Book::getId, Book::setId, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.version, Book.VERSION_COLUMN, Book::getVersion, Book::setVersion, Integer.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.updatedDate, Book.UPDATED_DATE_COLUMN, Book::getUpdatedDate, Book::setUpdatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.createdDate, Book.CREATED_DATE_COLUMN, Book::getCreatedDate, Book::setCreatedDate, Long.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.name, Book.Columns.NAME, Book::getName, Book::setName, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.title, Book.Columns.TITLE, Book::getTitle, Book::setTitle, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.authors, Book.Columns.AUTHORS, Book::getAuthors, Book::setAuthors, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.smallImageUrl, Book.Columns.SMALL_IMAGE_URL, Book::getSmallImageUrl, Book::setSmallImageUrl, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.isbn, Book.Columns.ISBN, Book::getIsbn, Book::setIsbn, String.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.isbn13, Book.Columns.ISBN13, Book::getIsbn13, Book::setIsbn13, BigInteger.class));
        tmp.add(new ValueFieldMeta<>(QBook.book.originalPublicationYear, Book.Columns.ORIGINAL_PUBLICATION_YEAR, Book::getOriginalPublicationYear, Book::setOriginalPublicationYear, Integer.class));

        String averageRatingFormulaOverride = "(select " +
                "avg(c." + Comment.Columns.RATING_COLUMN + ") " +
                "from " + Comment.TABLE + " c  " +
                "where c." + Comment.Columns.BOOK_ID_COLUMN + " = " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.ID_COLUMN + ") ";
        tmp.add(new ValueFieldMeta<>(QBook.book.averageRating, averageRatingFormulaOverride, Book::getAverageRating, Book::setAverageRating, Float.class));

        String ratingsCountFormulaOverride = "(select " +
                " (case when count(1)> 0 then count(1) else null end) " +
                " from " + Comment.TABLE + " c " +
                " where c." + Comment.Columns.BOOK_ID_COLUMN + " = " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.ID_COLUMN + " )";
        tmp.add(new ValueFieldMeta<>(QBook.book.ratingsCount, ratingsCountFormulaOverride, Book::getRatingsCount, Book::setRatingsCount, BigInteger.class));

        bookValueFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        final List<EntityFieldMeta<Book, QBook, ?, ?>> tmp = new ArrayList<>();
        tmp.add(new EntityFieldMeta<>(QBook.book.createdBy, Book.CREATED_BY_COLUMN, Book::getCreatedBy, Book::setCreatedBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QBook.book.lastUpdBy, Book.LAST_UPD_BY_COLUMN, Book::getLastUpdBy, Book::setLastUpdBy, UserRepositoryJdbc.userValueFieldsMeta, User.class, User::new));
        tmp.add(new EntityFieldMeta<>(QBook.book.lang, Book.Columns.LANG_ID, Book::getLang, Book::setLang, LangRepositoryJdbc.langValueFieldsMeta, Lang.class, Lang::new));
        tmp.add(new EntityFieldMeta<>(QBook.book.work, Book.Columns.WORK_ID, Book::getWork, Book::setWork, WorkRepositoryJdbc.workValueFieldsMeta, Work.class, Work::new));

        bookEntityFieldsMeta = ImmutableList.copyOf(tmp);
    }

    static {
        BOOK_SELECT_ALL_COLUMNS_SQL = "select " +
                bookValueFieldsMeta.stream().map(ValueFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                ", " +
                bookEntityFieldsMeta.stream().map(EntityFieldMeta::getFieldSelectSQLFragment).flatMap(Collection::stream).collect(Collectors.joining(" ,")) +
                " from " + Book.TABLE + " " + ValueFieldMeta.aliasQ(QBook.book) +
                " left join " + Lang.TABLE + " " + ValueFieldMeta.aliasQ(QBook.book.lang) +
                " on " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.Columns.LANG_ID + " = " + ValueFieldMeta.aliasQ(QBook.book.lang) + "." + Lang.ID_COLUMN +
                " left join " + Work.TABLE + " " + ValueFieldMeta.aliasQ(QBook.book.work) +
                " on " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.Columns.WORK_ID + " = " + ValueFieldMeta.aliasQ(QBook.book.work) + "." + Work.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QBook.book.createdBy) +
                " on " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.CREATED_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QBook.book.createdBy) + "." + User.ID_COLUMN +
                " left join " + User.TABLE + " " + ValueFieldMeta.aliasQ(QBook.book.lastUpdBy) +
                " on " + ValueFieldMeta.aliasQ(QBook.book) + "." + Book.LAST_UPD_BY_COLUMN + " = " + ValueFieldMeta.aliasQ(QBook.book.lastUpdBy) + "." + User.ID_COLUMN;
    }

    @Autowired
    public BookRepositoryJdbc(@NonNull NamedParameterJdbcOperations jdbcOp) {
        super(bookValueFieldsMeta, bookEntityFieldsMeta, BOOK_SELECT_ALL_COLUMNS_SQL, jdbcOp);
    }

    @Override
    public String getTable() {
        return Book.TABLE;
    }

    @Override
    public String getIdColumn() {
        return Book.ID_COLUMN;
    }

    @Override
    public Supplier<Book> getEntityFactory() {
        return Book::new;
    }
}
