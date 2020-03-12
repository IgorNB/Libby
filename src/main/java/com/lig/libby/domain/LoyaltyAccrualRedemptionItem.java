package com.lig.libby.domain;

import java.math.BigInteger;

public interface LoyaltyAccrualRedemptionItem  {
    String getId();

    User getLoyaltyMember();

    Task getTask();

    BigInteger getPoints();
}
