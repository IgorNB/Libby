package com.lig.libby.domain;

import com.lig.libby.domain.core.AbstractPersistentObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.math.BigInteger;


@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class BookMigration extends AbstractPersistentObject {
    public static final String TABLE = "WORK_BOOK_MIGRATION";

    private String bookId;

    private String bestBookId;

    private Integer booksCount;

    private BigInteger workRatingsCount;

    private BigInteger workTextReviewsCount;

    private String name;

    private String workId;

    private String languageCode;

    private String isbn;

    private BigInteger isbn13;

    private String authors;

    private Integer originalPublicationYear;

    private String originalTitle;

    private String title;

    private Float averageRating;

    private BigInteger ratingsCount;

    private BigInteger ratings1;

    private BigInteger ratings2;

    private BigInteger ratings3;

    private BigInteger ratings4;

    private BigInteger ratings5;

    private String imageUrl;

    private String smallImageUrl;

    public static final class Columns {
        public static final String BOOK_ID = "BOOK_ID";

        private Columns() {
        }
    }
}
