package com.lig.libby.domain;

import com.lig.libby.domain.core.GenericAbstractPersistentAuditingObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.jcip.annotations.NotThreadSafe;
import org.hibernate.annotations.Formula;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Document(collection = Lang.TABLE)
@Getter
@Setter
@ToString(callSuper = true)
public class Lang extends GenericAbstractPersistentAuditingObject<User> {
    public static final String TABLE = "LANG";
    public static final String NAME_COLUMN = "CODE";

    @Formula("NULL")
    private String q;

    @Field(NAME_COLUMN)
    private String code;
}
