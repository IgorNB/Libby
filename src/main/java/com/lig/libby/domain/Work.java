//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Work.TABLE)
@NotThreadSafe
@Setter
@Getter
@ToString(callSuper = true, exclude = {"bestBook"})
@FieldNameConstants
public class Work extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "WORK";

    @DBRef
    @JsonIgnore
    private Book bestBook;

    @Formula("NULL")
    private Integer booksCount;

    public static final class Columns {
        public static final String BEST_BOOK_ID_COLUMN = "bestBookId";

        private Columns() {
        }
    }
}
