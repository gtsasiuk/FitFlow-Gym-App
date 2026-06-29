package com.training.fitflow.config;

import feign.FeignException;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResilienceConfig {
    @Bean
    public CircuitBreakerConfigCustomizer workloadServiceCircuitBreakerCustomizer() {
        return CircuitBreakerConfigCustomizer.of("workloadService", builder ->
                builder.ignoreException(ex ->
                        ex instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500
                )
        );
    }
}