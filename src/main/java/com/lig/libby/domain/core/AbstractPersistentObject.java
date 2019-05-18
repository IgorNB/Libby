package com.lig.libby.domain.core;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;


/*
see

https://web.archive.org/web/20171211235806/http://www.onjava.com/pub/a/onjava/2006/09/13/dont-let-hibernate-steal-your-identity.html

and for modern annotation style

https://hibernate.atlassian.net/browse/HHH-4324?focusedCommentId=75539&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-75539
*/

@MappedSuperclass
@Setter
@Getter
@ToString
@FieldNameConstants
public abstract class AbstractPersistentObject implements PersistentObject {
    public static final String ID_COLUMN = "ID";
    public static final String VERSION_COLUMN = "VERSION";

    @Id
    @GeneratedValue(generator = "assigned")
    @GenericGenerator(name = "assigned", strategy = "org.hibernate.id.Assigned")
    @Column(name = ID_COLUMN, nullable = false)
    protected @NonNull String id;

    @Version
    @Column(name = VERSION_COLUMN, nullable = false)
    @ColumnDefault("0")
    private Integer version;


    public AbstractPersistentObject() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentObject)) {
            return false;
        }
        PersistentObject other = (PersistentObject) o;
        if (id == null) return false;
        return id.equals(other.getId());
    }

    public final int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return super.hashCode();
        }
    }

}
