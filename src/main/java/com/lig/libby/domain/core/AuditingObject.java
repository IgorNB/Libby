package com.lig.libby.domain.core;

public interface AuditingObject<U> {
    U getCreatedBy();

    Long getCreatedDate();

    U getLastUpdBy();

    Long getUpdatedDate();
}
