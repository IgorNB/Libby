package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@Document(collection = User.TABLE)
@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@FieldNameConstants
public class User extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "users";
    public static final String USER_AUTHORITY_JOIN_TABLE = "USER_AUTHORITY";
    public static final String USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN = "USER_ID";
    public static final String USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN = "AUTHORITY_ID";

    @Field(Columns.NAME_COLUMN)
    private String name;

    @Field(Columns.EMAIL_COLUMN)
    @Email
    private String email;

    @Field(Columns.IMAGE_URL_COLUMN)
    private String imageUrl;

    @Field(Columns.EMAIL_VERIFIED_COLUMN)
    private Boolean emailVerified = false;

    @Field(Columns.PASSWORD_COLUMN)
    @JsonIgnore
    private String password;


    @Field(Columns.PROVIDER_COLUMN)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Authority.AuthProvider provider;

    @Field(Columns.PROVIDER_ID_COLUMN)
    private String providerId;

    @DBRef
    @JsonIgnore
    private Set<Authority> authorities = new HashSet<>();

    @SuppressWarnings("S2068") //here we skip sonar rule "Credentials should not be hard-coded" due to this is just name of DB column
    public static final class Columns {
        public static final String NAME_COLUMN = "name";
        public static final String EMAIL_COLUMN = "email";
        public static final String EMAIL_VERIFIED_COLUMN = "emailVerified";
        public static final String PROVIDER_COLUMN = "provider";
        public static final String PASSWORD_COLUMN = "password";
        public static final String IMAGE_URL_COLUMN = "imageUrl";
        public static final String PROVIDER_ID_COLUMN = "providerId";

        private Columns() {
        }
    }
}
