package com.lig.libby.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.scheduling.PollerMetadata;

@IntegrationComponentScan
@ComponentScan
@Configuration
@EnableIntegration
public class LoyaltyEngineConfig {
    @Bean
    public QueueChannel loyaltyTransactionChannel() {
        return MessageChannels.queue(10).get();
    }

    @Bean
    public PublishSubscribeChannel loyaltyAccrualRedemptionItemChanel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean (name = PollerMetadata.DEFAULT_POLLER )
    public PollerMetadata poller () {
        return Pollers.fixedRate(100).maxMessagesPerPoll(2).get() ;
    }

    @Bean
    public IntegrationFlow loyaltyTransactionFlow() {
        return IntegrationFlows.from(loyaltyTransactionChannel())
                .split()
                .handle("loyaltyEngineService", "calculateAndSave")
                .aggregate()
                .channel(loyaltyAccrualRedemptionItemChanel())
                .get();
    }
}
