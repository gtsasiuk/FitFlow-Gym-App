package com.training.fitflow.util;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsernameGenerator {
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public String generate(String firstName, String lastName) {
        String base = firstName + "." + lastName;
        log.debug("Generating username for base={}", base);

        long count = countExisting(base);

        String result = count == 0 ? base : base + count;
        log.debug("Generated username={}", result);

        return result;
    }

    private long countExisting(String base) {
        long traineeCount = traineeRepository.findAll().stream()
                .filter(t -> {
                    String username = t.getUsername();
                    return username != null &&
                            (username.equals(base) || username.matches(base + "\\d+"));
                })
                .count();

        long trainerCount = trainerRepository.findAll().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        log.debug("Existing usernames count for {} = {}", base, traineeCount + trainerCount);

        return traineeCount + trainerCount;
    }
}