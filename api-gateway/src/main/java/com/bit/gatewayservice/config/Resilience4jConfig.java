package com.bit.gatewayservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> {
            CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                    .waitDurationInOpenState(Duration.ofSeconds(5))  // Set the wait duration to 2 seconds
                    .slidingWindowSize(10)
                    .failureRateThreshold(50.0f)
                    .permittedNumberOfCallsInHalfOpenState(5)
                    .automaticTransitionFromOpenToHalfOpenEnabled(true)
                    .minimumNumberOfCalls(10)
                    .build();

            TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                    .timeoutDuration(Duration.ofSeconds(3))  // Set the timeout duration to 3 seconds
                    .build();

            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(circuitBreakerConfig)
                    .timeLimiterConfig(timeLimiterConfig)
                    .build();
        });
    }
}
