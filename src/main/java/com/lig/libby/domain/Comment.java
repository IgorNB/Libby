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

@NotThreadSafe
@Entity
@Table(name = Comment.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class Comment extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "COMMENT";
    @Formula("NULL")
    private String q;
    @Column(name = Columns.RATING_COLUMN, nullable = true)
    private Integer rating;
    @Column(name = Columns.BODY_COLUMN, nullable = true)
    private String body;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne()
    @JoinColumn(name = Columns.BOOK_ID_COLUMN, nullable = false)
    private Book book;

    public static final class Columns {
        public static final String RATING_COLUMN = "RATING";
        public static final String BODY_COLUMN = "BODY";
        public static final String BOOK_ID_COLUMN = "BOOK_ID";

        private Columns() {
        }
    }
}
