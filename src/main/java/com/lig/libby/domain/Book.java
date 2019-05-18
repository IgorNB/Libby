package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.math.BigInteger;

@NotThreadSafe
@Entity
@Table(name = Book.TABLE,
        indexes = {@Index(columnList = "name"),
                @Index(columnList = "authors"),
                @Index(columnList = "title"),
                @Index(columnList = "original_publication_year"),
                @Index(columnList = "original_title"),
                @Index(columnList = "isbn"),
                @Index(columnList = "isbn13"),
                /*@Index(columnList = "average_rating"),
                @Index(columnList = "ratings_count"),*/
        }
)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class Book extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "BOOK";
    public static final String BOOK_TAG_JOIN_TABLE = "BOOK_TAG";
    public static final String BOOK_TAG_JOIN_TABLE_BOOK_FK = "BOOK_ID";
    public static final String BOOK_TAG_JOIN_TABLE_TAG_FK = "TAG_ID";
    @Formula("NULL")
    private String q;
    @Column(name = Columns.NAME, nullable = true)
    private String name;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.WORK_ID, nullable = false)
    private Work work;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.LANG_ID)
    private Lang lang;
    @Column(name = Columns.ISBN)
    private String isbn;
    @Column(name = Columns.ISBN13)
    private BigInteger isbn13;
    @Column(name = Columns.AUTHORS, length = 1000)
    private String authors;
    @Column(name = Columns.ORIGINAL_PUBLICATION_YEAR)
    private Integer originalPublicationYear;
    @Column(name = Columns.ORIGINAL_TITLE)
    private String originalTitle;
    @Column(name = Columns.TITLE)
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
    @Column(name = Columns.IMAGE_URL)
    private String imageUrl;
    @Column(name = Columns.SMALL_IMAGE_URL)
    private String smallImageUrl;

    public static final class Columns {
        public static final String ISBN = "isbn";
        public static final String ISBN13 = "isbn13";
        public static final String AUTHORS = "authors";
        public static final String ORIGINAL_PUBLICATION_YEAR = "original_publication_year";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String TITLE = "title";
        public static final String IMAGE_URL = "image_url";
        public static final String SMALL_IMAGE_URL = "small_image_url";
        public static final String NAME = "NAME";
        public static final String LANG_ID = "LANG_ID";
        public static final String WORK_ID = "WORK_ID";

        private Columns() {
        }
    }
}
