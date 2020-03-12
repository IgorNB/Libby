package com.lig.libby.service;

import com.lig.libby.domain.LoyaltyAccrualRedemptionItem;
import com.lig.libby.domain.LoyaltyTransaction;
import com.lig.libby.repository.LoyaltyTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class LoyaltyEngineService {

    private final LoyaltyTransactionRepository loyaltyTransactionRepository;

    @Autowired
    public LoyaltyEngineService(LoyaltyTransactionRepository loyaltyTransactionRepository) {
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
    }

    public LoyaltyAccrualRedemptionItem calculate(LoyaltyTransaction loyaltyTransaction) {
        loyaltyTransaction.setPoints(BigInteger.valueOf(10));
        return loyaltyTransaction;
    }

    public LoyaltyAccrualRedemptionItem calculateAndSave(LoyaltyTransaction loyaltyTransaction) {
        LoyaltyAccrualRedemptionItem item = calculate(loyaltyTransaction);
        loyaltyTransaction.setPoints(item.getPoints());
        loyaltyTransactionRepository.save(loyaltyTransaction);
        return loyaltyTransaction;
    }
}
