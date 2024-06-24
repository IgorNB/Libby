package com.lig.libby.repository;

import com.lig.libby.domain.Comment;
import com.lig.libby.domain.QComment;
import com.lig.libby.repository.core.GenericUiApiRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface CommentRepository extends MongoRepository<Comment, String>, QuerydslPredicateExecutor<Comment>, QuerydslBinderCustomizer<QComment>, GenericUiApiRepository<Comment> {
    default void customize(QuerydslBindings bindings, QComment root) {

    }
}