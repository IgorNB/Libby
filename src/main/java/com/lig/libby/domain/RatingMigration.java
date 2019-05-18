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

@NotThreadSafe
@Entity
@Table(name = RatingMigration.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class RatingMigration extends AbstractPersistentObject {
    public static final String TABLE = "RATING_MIGRATION";
    @Column(name = Columns.BOOK_ID_COLUMN)
    public String bookId;
    @Column(name = Columns.USER_ID_COLUMN)
    public String userId;
    @Column(name = Columns.RATING_COLUMN)
    public int rating;

    public static final class Columns {
        public static final String USER_ID_COLUMN = "user_Id";
        public static final String BOOK_ID_COLUMN = "book_Id";
        public static final String RATING_COLUMN = "rating";

        private Columns() {
        }
    }
}
