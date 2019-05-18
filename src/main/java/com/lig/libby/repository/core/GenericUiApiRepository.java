package com.lig.libby.repository.core;

import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

@Profile("never")
@SuppressWarnings("squid:S2326")
public interface GenericUiApiRepository<E, Q extends EntityPathBase<E>, I> extends JpaRepository<E, I>, QuerydslPredicateExecutor<E> {
}