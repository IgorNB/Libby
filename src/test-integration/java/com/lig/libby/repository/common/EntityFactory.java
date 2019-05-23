package com.lig.libby.repository.common;

public interface EntityFactory<T> {

    T getNewEntityInstance();
}
