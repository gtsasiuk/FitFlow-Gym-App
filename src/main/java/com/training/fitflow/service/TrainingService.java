package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.dao.TrainingDao;
import com.training.fitflow.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    public Training create(Training training) {
        trainerDao.findTrainerById(training.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        traineeDao.findTraineeById(training.getTraineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        return trainingDao.save(training);
    }

    public Training getById(Long id) {
        return trainingDao.findTrainingById(id)
                .orElseThrow(() -> new RuntimeException("Training not found"));
    }

    public List<Training> getAll() {
        return trainingDao.findAllTrainings();
    }
}
