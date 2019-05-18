package com.lig.libby.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import net.jcip.annotations.NotThreadSafe;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@NotThreadSafe
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = User.TABLE,
        uniqueConstraints = {
                @UniqueConstraint(columnNames = User.Columns.EMAIL_COLUMN)
        })
@FieldNameConstants
public class User extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "users";
    public static final String USER_AUTHORITY_JOIN_TABLE = "USER_AUTHORITY";
    public static final String USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN = "USER_ID";
    public static final String USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN = "AUTHORITY_ID";
    @Column(name = Columns.NAME_COLUMN, nullable = false)
    private String name;
    @Email
    @Column(name = Columns.EMAIL_COLUMN, nullable = false)
    private String email;
    @Column(name = Columns.IMAGE_URL_COLUMN)
    private String imageUrl;
    @Column(name = Columns.EMAIL_VERIFIED_COLUMN, nullable = false)
    private Boolean emailVerified = false;
    @JsonIgnore
    @Column(name = Columns.PASSWORD_COLUMN)
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = Columns.PROVIDER_COLUMN)
    private Authority.AuthProvider provider;
    @Column(name = Columns.PROVIDER_ID_COLUMN)
    private String providerId;
    @JsonIgnore
    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = USER_AUTHORITY_JOIN_TABLE,
            joinColumns = {@JoinColumn(name = USER_AUTHORITY_JOIN_TABLE_USER_FK_COLUMN, referencedColumnName = ID_COLUMN)},
            inverseJoinColumns = {@JoinColumn(name = USER_AUTHORITY_JOIN_TABLE_AUTHORITY_FK_COLUMN, referencedColumnName = ID_COLUMN)})
    private Set<Authority> authorities = new HashSet<>();

    public static final class Columns {
        public static final String NAME_COLUMN = "name";
        public static final String EMAIL_COLUMN = "email";
        public static final String EMAIL_VERIFIED_COLUMN = "email_verified";
        public static final String PROVIDER_COLUMN = "provider";
        public static final String PASSWORD_COLUMN = "password";
        public static final String IMAGE_URL_COLUMN = "IMAGE_URL";
        public static final String PROVIDER_ID_COLUMN = "PROVIDER_ID";

        private Columns() {
        }
    }
}
