package com.lig.libby.repository;

import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import com.lig.libby.repository.core.GenericUiApiRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface LangRepository extends MongoRepository<Lang, String>, QuerydslPredicateExecutor<Lang>, QuerydslBinderCustomizer<QLang>, GenericUiApiRepository<Lang> {
    default void customize(QuerydslBindings bindings, QLang root) {
        bindings.bind(root.q).first((path, value) -> root.code.startsWith(value.toLowerCase()));
    }
}