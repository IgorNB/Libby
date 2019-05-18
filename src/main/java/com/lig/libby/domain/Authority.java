package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.jcip.annotations.NotThreadSafe;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import static com.lig.libby.domain.Authority.NAME_COLUMN;

@NotThreadSafe
@Entity
@Table(name = Authority.TABLE, indexes = {@Index(columnList = NAME_COLUMN, unique = true)})
@Getter
@Setter
@ToString(callSuper = true)
public class Authority extends GenericAbstractPersistentAuditingObject<User> {

    public static final String TABLE = "AUTHORITY";
    public static final String NAME_COLUMN = "NAME";

    @Column(name = NAME_COLUMN, nullable = false)
    private String name;


    @SuppressWarnings("squid:S00115")
    public enum AuthProvider {
        local,
        facebook,
        google,
        github
    }

    //All Authority.name values used in java code should be listed here,
    // but for now there could be Authority.name values listed in DB and not used in java code,
    // so Authority.name could be wider then Authority.Roles
    public final class Roles {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
        public static final String ANONYMOUS = "ROLE_ANONYMOUS";

        private Roles() {
        }
    }
}
