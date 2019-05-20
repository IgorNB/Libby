package com.lig.libby.config;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrixDashboard
@EnableCircuitBreaker
public class CircuitBreakerConfig {
}
