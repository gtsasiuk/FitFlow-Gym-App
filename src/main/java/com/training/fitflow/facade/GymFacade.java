package com.training.fitflow.facade;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.service.TraineeService;
import com.training.fitflow.service.TrainerService;
import com.training.fitflow.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
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
        log.info("Facade: createTrainee request for {} {}", trainee.getFirstName(), trainee.getLastName());

        Trainee result = traineeService.create(trainee);

        log.info("Facade: trainee created id={}", result.getId());

        return result;
    }

    public Trainee getTrainee(String username) {
        log.debug("Facade: getTrainee username={}", username);
        return traineeService.getByUsername(username);
    }

    public List<Trainee> getAllTrainees() {
        log.debug("Facade: getAllTrainees request");
        return traineeService.getAll();
    }

    public void deleteTrainee(String username) {
        log.info("Facade: deleteTrainee id={}", username);
        traineeService.deleteByUsername(username);
    }

    public Trainer createTrainer(Trainer trainer) {
        log.info("Facade: createTrainer request for {} {}", trainer.getFirstName(), trainer.getLastName());

        Trainer result = trainerService.create(trainer);

        log.info("Facade: trainer created id={}", result.getId());

        return result;
    }

    public Trainer getTrainer(String username) {
        log.debug("Facade: getTrainer id={}", username);
        return trainerService.getByUsername(username);
    }

    public List<Trainer> getAllTrainers() {
        log.debug("Facade: getAllTrainers request");
        return trainerService.getAll();
    }

    public Training createTraining(Training training) {
        log.info("Facade: createTraining request name={}, trainerId={}, traineeId={}",
                training.getName(),
                training.getTrainer(),
                training.getTrainer()
        );

        Training result = trainingService.create(training);

        log.info("Facade: training created id={}", result.getId());

        return result;
    }

    public Training getTraining(Long id) {
        log.debug("Facade: getTraining id={}", id);
        return trainingService.getById(id);
    }

    public List<Training> getAllTrainings() {
        log.debug("Facade: getAllTrainings request");
        return trainingService.getAll();
    }
}