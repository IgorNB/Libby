package com.lig.libby.repository.common;

import org.springframework.transaction.annotation.Transactional;

public interface EntityFactory<T> {

    T getNewEntityInstance();
}
