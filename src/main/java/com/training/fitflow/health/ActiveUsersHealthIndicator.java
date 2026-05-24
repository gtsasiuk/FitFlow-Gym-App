package com.training.fitflow.health;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("activeUsers")
@RequiredArgsConstructor
@Slf4j
public class ActiveUsersHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public Health health() {
        try {
            long totalTrainers = trainerRepository.count();
            long totalTrainees = traineeRepository.count();

            long activeTrainers = trainerRepository.countByActiveTrue();
            long activeTrainees = traineeRepository.countByActiveTrue();

            log.debug("Health check: trainers active={}/{}, trainees active={}/{}",
                    activeTrainers, totalTrainers, activeTrainees, totalTrainees);

            if (totalTrainers > 0 && activeTrainers == 0) {
                log.warn("Health check warning: all trainers are deactivated");
                return Health.status("DEGRADED")
                        .withDetail("reason", "All trainers are deactivated")
                        .withDetail("totalTrainers", totalTrainers)
                        .withDetail("activeTrainers", 0)
                        .build();
            }

            return Health.up()
                    .withDetail("totalTrainers", totalTrainers)
                    .withDetail("activeTrainers", activeTrainers)
                    .withDetail("totalTrainees", totalTrainees)
                    .withDetail("activeTrainees", activeTrainees)
                    .build();
        } catch (Exception e) {
            log.error("Health check error for active users", e);
            return Health.down(e).build();
        }
    }
}