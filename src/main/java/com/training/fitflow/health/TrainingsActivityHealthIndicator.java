package com.training.fitflow.health;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("trainingsActivity")
@RequiredArgsConstructor
@Slf4j
public class TrainingsActivityHealthIndicator implements HealthIndicator {
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public Health health() {
        try {
            long trainings = trainingRepository.count();
            long trainers = trainerRepository.count();
            long trainees = traineeRepository.count();

            log.debug("Activity check: trainings={}, trainers={}, trainees={}",
                    trainings, trainers, trainees);

            if (trainers > 0 && trainees > 0 && trainings == 0) {
                log.warn("Health check: users exist but no trainings yet");
                return Health.status("WARNING")
                        .withDetail("reason", "Users exist but no trainings created yet")
                        .withDetail("trainings", 0)
                        .withDetail("trainers", trainers)
                        .withDetail("trainees", trainees)
                        .build();
            }

            return Health.up()
                    .withDetail("trainings", trainings)
                    .withDetail("trainers", trainers)
                    .withDetail("trainees", trainees)
                    .build();

        } catch (Exception e) {
            log.error("Health check error for trainings activity", e);
            return Health.down(e).build();
        }
    }
}
