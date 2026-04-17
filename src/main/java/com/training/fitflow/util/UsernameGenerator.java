package com.training.fitflow.util;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.dao.TrainerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
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

        long count = countExisting(base);

        return count == 0 ? base : base + count;
    }

    private long countExisting(String base) {
        long traineeCount = traineeDao.findAllTrainees().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        long trainerCount = trainerDao.findAllTrainers().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        return traineeCount + trainerCount;
    }
}