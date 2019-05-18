package com.lig.libby.repository;

import com.lig.libby.domain.Authority;
import com.lig.libby.domain.QUser;
import com.lig.libby.domain.User;
import com.querydsl.core.BooleanBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

@Profile("springDataJpa")
public interface UserRepository extends JpaRepository<User, String>, QuerydslPredicateExecutor<User>, QuerydslBinderCustomizer<QUser> {
    default void customize(QuerydslBindings bindings, QUser root) {

    }

    Optional<User> findByEmail(String email);

    default User findFirstWithAdminAuthority() {
        BooleanBuilder where = new BooleanBuilder().and(QUser.user.authorities.any().name.eq(Authority.Roles.ADMIN));
        return this.findAll(where).iterator().next();
    }
}
