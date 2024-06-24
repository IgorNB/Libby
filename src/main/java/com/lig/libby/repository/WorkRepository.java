package com.lig.libby.repository;

import com.lig.libby.domain.QWork;
import com.lig.libby.domain.Work;
import com.lig.libby.repository.core.GenericUiApiRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface WorkRepository extends MongoRepository<Work, String>, QuerydslPredicateExecutor<Work>, QuerydslBinderCustomizer<QWork>, GenericUiApiRepository<Work> {
    default void customize(QuerydslBindings bindings, QWork root) {

    }
}
