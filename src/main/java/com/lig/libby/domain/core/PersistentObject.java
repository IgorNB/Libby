package com.lig.libby.domain.core;

import java.io.Serializable;

public interface PersistentObject extends Serializable {
    String getId();

    void setId(String id);

    Integer getVersion();

    void setVersion(Integer version);
}
