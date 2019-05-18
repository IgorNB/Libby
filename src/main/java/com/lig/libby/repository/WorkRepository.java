package com.lig.libby.repository;

import com.lig.libby.domain.QWork;
import com.lig.libby.domain.Work;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface WorkRepository extends JpaRepository<Work, String>, QuerydslPredicateExecutor<Work>, QuerydslBinderCustomizer<QWork> {
    default void customize(QuerydslBindings bindings, QWork root) {

    }
}
