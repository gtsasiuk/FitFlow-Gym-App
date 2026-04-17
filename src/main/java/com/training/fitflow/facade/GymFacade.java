package com.training.fitflow.facade;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.service.TraineeService;
import com.training.fitflow.service.TrainerService;
import com.training.fitflow.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Trainee getTrainee(Long id) {
        return traineeService.getById(id);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.getAll();
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteById(id);
    }

    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Trainer getTrainer(Long id) {
        return trainerService.getById(id);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAll();
    }


    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Training getTraining(Long id) {
        return trainingService.getById(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAll();
    }
}
