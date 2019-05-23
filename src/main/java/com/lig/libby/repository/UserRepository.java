package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.Task;
import com.lig.libby.domain.User;
import com.lig.libby.repository.core.GenericUiApiRepository;
import com.querydsl.core.BooleanBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

@Profile("springDataJpa")
public interface UserRepository extends MongoRepository<User, String>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser>, GenericUiApiRepository<User> {
    default void customize(QuerydslBindings bindings, QUser root) {

    }

    Optional<User> findByEmail(String email);

    default User findFirstWithAdminAuthority() {
        BooleanBuilder where = new BooleanBuilder().and(QUser.user.name.eq("admin"));
        return this.findAll(where).iterator().next();
    }
}
