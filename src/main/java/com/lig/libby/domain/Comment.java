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

@Document(collection = Comment.TABLE)
@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class Comment extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "COMMENT";
    @Formula("NULL")
    private String q;

    @Field(Columns.RATING_COLUMN)
    private Integer rating;

    @Field(Columns.BODY_COLUMN)
    private String body;

    @DBRef
    private Book book;

    public static final class Columns {
        public static final String RATING_COLUMN = "rating";
        public static final String BODY_COLUMN = "body";
        public static final String BOOK_ID_COLUMN = "bookId";

        private Columns() {
        }
    }
}
