package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@NotThreadSafe
@Entity
@Table(name = Lang.TABLE, indexes = {@Index(columnList = "CODE")})
@Getter
@Setter
@ToString(callSuper = true)
public class Lang extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "LANG";
    public static final String NAME_COLUMN = "CODE";

    @Formula("NULL")
    private String q;

    @Column(name = NAME_COLUMN, nullable = false)
    private String code;
}
