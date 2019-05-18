package com.lig.libby.domain;

import com.lig.libby.domain.core.AbstractPersistentObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;

@NotThreadSafe
@Entity
@Table(name = BookMigration.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class BookMigration extends AbstractPersistentObject {
    public static final String TABLE = "WORK_BOOK_MIGRATION";
    @Column(name = Columns.BOOK_ID)
    private String bookId;
    @Column(name = Columns.BEST_BOOK_ID)
    private String bestBookId;
    @Column(name = Columns.BOOKS_COUNT)
    private Integer booksCount;
    @Column(name = Columns.WORK_RATINGS_COUNT)
    private BigInteger workRatingsCount;
    @Column(name = Columns.WORK_TEXT_REVIEWS_COUNT)
    private BigInteger workTextReviewsCount;
    @Column(name = Columns.NAME)
    private String name;
    @Column(name = Columns.WORK_ID, nullable = false)
    private String workId;
    @Column(name = Columns.LANGUAGE_CODE)
    private String languageCode;
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
    @Column(name = Columns.AVERAGE_RATING)
    private Float averageRating;
    @Column(name = Columns.RATINGS_COUNT)
    private BigInteger ratingsCount;
    @Column(name = Columns.RATINGS_1)
    private BigInteger ratings1;
    @Column(name = Columns.RATINGS_2)
    private BigInteger ratings2;
    @Column(name = Columns.RATINGS_3)
    private BigInteger ratings3;
    @Column(name = Columns.RATINGS_4)
    private BigInteger ratings4;
    @Column(name = Columns.RATINGS_5)
    private BigInteger ratings5;
    @Column(name = Columns.IMAGE_URL)
    private String imageUrl;
    @Column(name = Columns.SMALL_IMAGE_URL)
    private String smallImageUrl;

    public static final class Columns {
        public static final String NAME = "NAME";
        public static final String BOOK_ID = "BOOK_ID";
        public static final String BEST_BOOK_ID = "BEST_BOOK_ID";
        public static final String WORK_ID = "WORK_ID";
        public static final String BOOKS_COUNT = "books_count";
        public static final String WORK_RATINGS_COUNT = "work_ratings_count";
        public static final String WORK_TEXT_REVIEWS_COUNT = "work_text_reviews_count";
        public static final String LANGUAGE_CODE = "LANGUAGE_CODE";
        public static final String ISBN = "isbn";
        public static final String ISBN13 = "isbn13";
        public static final String AUTHORS = "authors";
        public static final String ORIGINAL_PUBLICATION_YEAR = "original_publication_year";
        public static final String ORIGINAL_TITLE = "original_title";
        public static final String TITLE = "title";
        public static final String AVERAGE_RATING = "average_rating";
        public static final String RATINGS_COUNT = "ratings_count";
        public static final String RATINGS_1 = "ratings_1";
        public static final String RATINGS_2 = "ratings_2";
        public static final String RATINGS_3 = "ratings_3";
        public static final String RATINGS_4 = "ratings_4";
        public static final String RATINGS_5 = "ratings_5";
        public static final String IMAGE_URL = "image_url";
        public static final String SMALL_IMAGE_URL = "small_image_url";

        private Columns() {
        }
    }
}
