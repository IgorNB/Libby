package com.lig.libby.repository;

import com.lig.libby.domain.Book;
import com.lig.libby.domain.QBook;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface BookRepository extends JpaRepository<Book, String>, QuerydslPredicateExecutor<Book>, QuerydslBinderCustomizer<QBook> {
    default void customize(QuerydslBindings bindings, QBook root) {
        bindings.bind(root.authors).first((path, value) -> path.startsWithIgnoreCase(value));
        bindings.bind(root.title).first((path, value) -> path.startsWithIgnoreCase(value));
        bindings.bind(root.originalTitle).first((path, value) -> path.startsWithIgnoreCase(value));
        bindings.bind(root.q).first((path, value) -> root.title.startsWithIgnoreCase(value));
    }
}