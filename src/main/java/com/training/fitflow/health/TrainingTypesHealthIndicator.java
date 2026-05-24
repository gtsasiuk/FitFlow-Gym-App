package com.training.fitflow.health;

import com.training.fitflow.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("trainingTypes")
@RequiredArgsConstructor
@Slf4j
public class TrainingTypesHealthIndicator implements HealthIndicator {
    private static final int MIN_EXPECTED_TYPES = 5;

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        try {
            long count = trainingTypeRepository.count();
            log.debug("Health check: training types count={}", count);

            if (count < MIN_EXPECTED_TYPES) {
                log.error("Health check failed: no training types in DB, system cannot create trainers");
                return Health.down()
                        .withDetail("reason", "No training types found - reference data is missing")
                        .withDetail("count", count)
                        .withDetail("minimumExpected", MIN_EXPECTED_TYPES)
                        .build();
            }

            return Health.up()
                    .withDetail("count", count)
                    .build();
        } catch (Exception e) {
            log.error("Health check error for training types", e);
            return Health.down(e).build();
        }
    }
}
