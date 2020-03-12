package com.lig.libby.repository;

import com.lig.libby.domain.LoyaltyTransaction;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("springDataJpa")
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, String> {
}