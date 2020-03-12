package com.lig.libby.service;


import com.lig.libby.domain.LoyaltyAccrualRedemptionItem;
import com.lig.libby.domain.LoyaltyTransaction;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.util.Collection;

@MessagingGateway
public interface LoyaltyEngineGateway {

    @Gateway(requestChannel = "loyaltyTransactionChannel", replyChannel = "loyaltyAccrualRedemptionItemChanel")
    Collection<LoyaltyAccrualRedemptionItem> process(Collection<LoyaltyTransaction> orderItem);
}
