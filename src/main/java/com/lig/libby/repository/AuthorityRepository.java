package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QAuthority;
import com.lig.libby.repository.core.GenericUiApiRepository;
import lombok.NonNull;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface AuthorityRepository extends MongoRepository<Authority, String>, QuerydslPredicateExecutor<Authority>, QuerydslBinderCustomizer<QAuthority>, GenericUiApiRepository<Authority> {
    default void customize(QuerydslBindings bindings, QAuthority root) {
    }


    Authority getAuthorityByName(@NonNull String name);
}