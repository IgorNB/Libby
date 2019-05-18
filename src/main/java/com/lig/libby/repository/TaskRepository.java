package com.lig.libby.repository;

import com.lig.libby.domain.QTask;
import com.lig.libby.domain.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface TaskRepository extends JpaRepository<Task, String>, QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask> {
    @Override
    default void customize(QuerydslBindings bindings, QTask root) {
        bindings.bind(root.q).first((path, value) -> root.id.eq(value));
    }
}