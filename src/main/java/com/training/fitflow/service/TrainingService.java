package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.dao.TrainingDao;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    public Training create(Training training) {
        log.info("Creating training: name={}, trainerId={}, traineeId={}",
                training.getName(),
                training.getTrainerId(),
                training.getTraineeId()
        );

        trainerDao.findTrainerById(training.getTrainerId())
                .orElseThrow(() -> {
                    log.warn("Trainer not found id={}", training.getTrainerId());
                    return new TrainerNotFoundException(training.getTrainerId());
                });

        traineeDao.findTraineeById(training.getTraineeId())
                .orElseThrow(() -> {
                    log.warn("Trainee not found id={}", training.getTraineeId());
                    return new TraineeNotFoundException(training.getTraineeId());
                });

        Training saved = trainingDao.save(training);

        log.info("Training created successfully with id={}", saved.getId());

        return saved;
    }

    public Training getById(Long id) {
        log.debug("Fetching training by id={}", id);
        return trainingDao.findTrainingById(id)
                .orElseThrow(() -> {
                    log.warn("Training not found id={}", id);
                    return new TrainingNotFoundException(id);
                });
    }

    public List<Training> getAll() {
        log.debug("Fetching all trainings");
        return trainingDao.findAllTrainings();
    }
}
