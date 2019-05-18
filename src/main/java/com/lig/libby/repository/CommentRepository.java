package com.lig.libby.repository;

import com.lig.libby.domain.Comment;
import com.lig.libby.domain.QComment;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface CommentRepository extends JpaRepository<Comment, String>, QuerydslPredicateExecutor<Comment>, QuerydslBinderCustomizer<QComment> {
    default void customize(QuerydslBindings bindings, QComment root) {

    }
}