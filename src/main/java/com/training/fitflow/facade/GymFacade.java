package com.training.fitflow.facade;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.service.AuthService;
import com.training.fitflow.service.TraineeService;
import com.training.fitflow.service.TrainerService;
import com.training.fitflow.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GymFacade {
    private final AuthService authService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(AuthService authService, TraineeService traineeService,
                     TrainerService trainerService, TrainingService trainingService) {
        this.authService = authService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    private Trainee loginTrainee(String username, String password) {
        return authService.authenticateTrainee(username, password);
    }

    private Trainer loginTrainer(String username, String password) {
        return authService.authenticateTrainer(username, password);
    }

    public Trainee createTrainee(Trainee trainee) {
        log.info("Facade: createTrainee request for {} {}", trainee.getFirstName(), trainee.getLastName());

        Trainee result = traineeService.create(trainee);

        log.info("Facade: trainee created id={}", result.getId());

        return result;
    }

    public Trainee updateTrainee(String username, String password, Trainee updatedData) {
        loginTrainee(username, password);

        log.info("Facade: updateTrainee request for id={}", updatedData.getId());
        Trainee result = traineeService.update(updatedData);
        log.info("Facade: trainee updated id={}", result.getId());

        return result;
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        Trainee authTrainee = authService.authenticateTrainee(username, oldPassword);
        log.info("Facade: change trainee password request for id={}", authTrainee.getId());
        traineeService.changePassword(authTrainee.getUsername(), newPassword);
    }

    public void activateTrainee(String username, String password) {
        Trainee authTrainee = loginTrainee(username, password);
        log.info("Facade: activate trainee request for id={}", authTrainee.getId());
        traineeService.activate(authTrainee.getUsername());
    }

    public void deactivateTrainee(String username, String password) {
        Trainee authTrainee = loginTrainee(username, password);
        log.info("Facade: deactivate trainee request for id={}", authTrainee.getId());
        traineeService.deactivate(authTrainee.getUsername());
    }

    public Trainee getTrainee(String username, String password) {
        Trainee authTrainee = loginTrainee(username, password);
        log.debug("Facade: getTrainee username={}", username);
        return traineeService.getByUsername(authTrainee.getUsername());
    }

    public List<Trainee> getAllTrainees(String username, String password) {
        loginTrainee(username, password);
        log.debug("Facade: getAllTrainees request");
        return traineeService.getAll();
    }

    public void deleteTrainee(String username, String password) {
        Trainee authTrainee = loginTrainee(username, password);
        log.info("Facade: deleteTrainee id={}", username);
        traineeService.deleteByUsername(authTrainee.getUsername());
    }

    public Trainer createTrainer(Trainer trainer) {
        log.info("Facade: createTrainer request for {} {}", trainer.getFirstName(), trainer.getLastName());

        Trainer result = trainerService.create(trainer);

        log.info("Facade: trainer created id={}", result.getId());

        return result;
    }

    public Trainer updateTrainer(String username, String password, Trainer updatedData) {
        loginTrainer(username, password);

        log.info("Facade: updateTrainer request for id={}", updatedData.getId());
        Trainer result = trainerService.update(updatedData);
        log.info("Facade: trainer updated id={}", result.getId());

        return result;
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        Trainer authTrainer = authService.authenticateTrainer(username, oldPassword);
        log.info("Facade: change trainer password request for id={}", authTrainer.getId());
        traineeService.changePassword(authTrainer.getUsername(), newPassword);
    }

    public void activateTrainer(String username, String password) {
        Trainer authTrainer = loginTrainer(username, password);
        log.info("Facade: activate user request for id={}", authTrainer.getId());
        traineeService.activate(authTrainer.getUsername());
    }

    public void deactivateTrainer(String username, String password) {
        Trainer authTrainer = loginTrainer(username, password);
        log.info("Facade: deactivate user request for id={}", authTrainer.getId());
        traineeService.deactivate(authTrainer.getUsername());
    }

    public Trainer getTrainer(String username, String password) {
        Trainer authTrainer = loginTrainer(username, password);
        log.debug("Facade: getTrainer id={}", username);
        return trainerService.getByUsername(authTrainer.getUsername());
    }

    public List<Trainer> getAllTrainers(String username, String password) {
        loginTrainer(username, password);
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