package com.lig.libby.repository;

import com.lig.libby.domain.Lang;
import com.lig.libby.domain.QTask;
import com.lig.libby.domain.Task;
import com.lig.libby.repository.core.GenericUiApiRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface TaskRepository extends MongoRepository<Task, String>, QuerydslPredicateExecutor<Task>, QuerydslBinderCustomizer<QTask>, GenericUiApiRepository<Task> {
    @Override
    default void customize(QuerydslBindings bindings, QTask root) {
        bindings.bind(root.q).first((path, value) -> root.id.eq(value));
    }
}