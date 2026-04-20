package com.training.fitflow.util;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.dao.TrainerDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UsernameGenerator {
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
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
        long traineeCount = traineeDao.findAllTrainees().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        long trainerCount = trainerDao.findAllTrainers().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        log.debug("Existing usernames count for {} = {}", base, traineeCount + trainerCount);

        return traineeCount + trainerCount;
    }
}