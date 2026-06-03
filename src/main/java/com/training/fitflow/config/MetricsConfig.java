package com.training.fitflow.config;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    @Bean
    public Counter loginSuccessCounter(MeterRegistry registry) {
        return Counter.builder("fitflow.auth.login")
                .description("Login attempts")
                .tag("result", "success")
                .register(registry);
    }

    @Bean
    public Counter loginFailureCounter(MeterRegistry registry) {
        return Counter.builder("fitflow.auth.login")
                .description("Login attempts")
                .tag("result", "failure")
                .register(registry);
    }

    @Bean
    public Gauge activeTrainersGauge(MeterRegistry registry, TrainerRepository trainerRepo) {
        return Gauge.builder("fitflow.users.active",
                        trainerRepo,
                        repo -> repo.countByActiveTrue())
                .description("Number of currently active users")
                .tag("type", "trainer")
                .register(registry);
    }

    @Bean
    public Gauge activeTraineesGauge(MeterRegistry registry, TraineeRepository traineeRepo) {
        return Gauge.builder("fitflow.users.active",
                        traineeRepo,
                        repo -> repo.countByActiveTrue())
                .description("Number of currently active users")
                .tag("type", "trainee")
                .register(registry);
    }
}
