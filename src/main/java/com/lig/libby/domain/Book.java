package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;

@Document(collection = Book.TABLE)
@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class Book extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "BOOK";
    public static final String BOOK_TAG_JOIN_TABLE = "bookTag";
    public static final String BOOK_TAG_JOIN_TABLE_BOOK_FK = "bookId";
    public static final String BOOK_TAG_JOIN_TABLE_TAG_FK = "tagId";
    @Formula("NULL")
    private String q;


    @Field(Columns.NAME)
    private String name;

    @DBRef
    private Work work;

    @DBRef
    private Lang lang;

    @Field(Columns.ISBN)
    private String isbn;

    @Field(Columns.ISBN13)
    private BigInteger isbn13;

    @Field(Columns.AUTHORS)
    private String authors;

    @Field(Columns.ORIGINAL_PUBLICATION_YEAR)
    private Integer originalPublicationYear;

    @Field(Columns.ORIGINAL_TITLE)
    private String originalTitle;

    @Field(Columns.TITLE)
    private String title;

    @Formula("(" +
            " select avg(c." + Comment.Columns.RATING_COLUMN + ")" +
            " from " + Comment.TABLE + " c" +
            " where c." + Comment.Columns.BOOK_ID_COLUMN + " = " + Book.ID_COLUMN +
            " )")
    private Float averageRating;

    @Formula("(" +
            " select count(1) " +
            " from " + Comment.TABLE + " c " +
            " where c." + Comment.Columns.BOOK_ID_COLUMN + " = " + Book.ID_COLUMN +
            " )")
    private BigInteger ratingsCount;

    @Formula("NULL")
    private BigInteger ratings1;
    @Formula("NULL")
    private BigInteger ratings2;
    @Formula("NULL")
    private BigInteger ratings3;
    @Formula("NULL")
    private BigInteger ratings4;
    @Formula("NULL")
    private BigInteger ratings5;

    @Field(Columns.IMAGE_URL)
    private String imageUrl;

    @Field(Columns.SMALL_IMAGE_URL)
    private String smallImageUrl;

    public static final class Columns {
        public static final String ISBN = "isbn";
        public static final String ISBN13 = "isbn13";
        public static final String AUTHORS = "authors";
        public static final String ORIGINAL_PUBLICATION_YEAR = "originalPublicationYear";
        public static final String ORIGINAL_TITLE = "originalTitle";
        public static final String TITLE = "title";
        public static final String IMAGE_URL = "imageUrl";
        public static final String SMALL_IMAGE_URL = "smallImageUrl";
        public static final String NAME = "name";
        public static final String LANG_ID = "langId";
        public static final String WORK_ID = "workId";

        private Columns() {
        }
    }
}
