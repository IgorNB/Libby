package com.lig.libby.domain.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;


@MappedSuperclass
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@FieldNameConstants
@ToString(callSuper = true)
public abstract class GenericAbstractPersistentAuditingObject<U extends PersistentObject> extends AbstractPersistentObject implements PersistentObject, AuditingObject<U> {

    public static final String CREATED_BY_COLUMN = "CREATED_BY_USER_ID";
    public static final String CREATED_DATE_COLUMN = "created_date";
    public static final String LAST_UPD_BY_COLUMN = "LAST_UPD_BY_USER_ID";
    public static final String UPDATED_DATE_COLUMN = "updated_date";

    @DBRef
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CREATED_BY_COLUMN, updatable = false)

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdBy", "lastUpdBy"})
    private U createdBy;


    @CreatedDate
    @Column(name = CREATED_DATE_COLUMN, updatable = false)
    @JsonIgnore
    private Long createdDate/* = Instant.now().toEpochMilli()*/;


    @DBRef
    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LAST_UPD_BY_COLUMN)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "createdBy", "lastUpdBy"})
    private U lastUpdBy;


    @LastModifiedDate
    @Column(name = UPDATED_DATE_COLUMN)
    @JsonIgnore
    private Long updatedDate/* = Instant.now().toEpochMilli()*/;

}
