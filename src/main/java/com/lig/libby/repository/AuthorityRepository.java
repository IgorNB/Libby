package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QAuthority;
import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.transaction.annotation.Transactional;

@Profile("springDataJpa")
public interface AuthorityRepository extends JpaRepository<Authority, String>, QuerydslPredicateExecutor<Authority>, QuerydslBinderCustomizer<QAuthority> {
    default void customize(QuerydslBindings bindings, QAuthority root) {
    }

    @Transactional
    Authority getAuthorityByName(@NonNull String name);
}