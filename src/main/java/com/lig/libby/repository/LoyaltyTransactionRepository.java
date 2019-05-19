package com.lig.libby.repository;

import com.lig.libby.domain.LoyaltyTransaction;
import com.lig.libby.domain.QLoyaltyTransaction;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

@Profile("springDataJpa")
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, String>, QuerydslPredicateExecutor<LoyaltyTransaction>, QuerydslBinderCustomizer<QLoyaltyTransaction> {
    @Override
    default void customize(QuerydslBindings bindings, QLoyaltyTransaction root) {
        bindings.bind(root.q).first((path, value) -> root.id.eq(value));
    }
}