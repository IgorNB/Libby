package com.lig.libby.repository;

import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QLang;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface LangRepository extends JpaRepository<Lang, String>, QuerydslPredicateExecutor<Lang>, QuerydslBinderCustomizer<QLang> {
    default void customize(QuerydslBindings bindings, QLang root) {
        bindings.bind(root.q).first((path, value) -> root.code.startsWith(value.toLowerCase()));
    }
}