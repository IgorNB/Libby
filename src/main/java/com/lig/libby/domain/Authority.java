package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.jcip.annotations.NotThreadSafe;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = Authority.TABLE)
@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
public class Authority extends GenericAbstractPersistentAuditingObject<User> {

    public static final String TABLE = "AUTHORITY";
    public static final String NAME_COLUMN = "NAME";

    @Field(NAME_COLUMN)
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
