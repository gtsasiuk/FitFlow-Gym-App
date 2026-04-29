package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.model.Training;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
import com.training.fitflow.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    @Transactional
    public Training create(Training training) {
        log.info("Creating training: name={}, trainerId={}, traineeId={}",
                training.getName(),
                training.getTrainer().getId(),
                training.getTrainee().getId()
        );

        validateTraining(training);

        trainerRepository.findById(training.getTrainer().getId())
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", training.getTrainer().getUsername());
                    return new TrainerNotFoundException(training.getTrainer().getUsername());
                });

        traineeRepository.findById(training.getTrainee().getId())
                .orElseThrow(() -> {
                    log.warn("Trainee not found username={}", training.getTrainee().getUsername());
                    return new TraineeNotFoundException(training.getTrainee().getUsername());
                });

        Training saved = trainingRepository.save(training);

        log.info("Training created successfully with id={}", saved.getId());

        return saved;
    }

    public List<Training> getTraineeTrainings(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long typeId
    ) {
        log.info("Getting trainee trainings username={}", username);

        traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        return trainingRepository.findTraineeTrainings(
                username, fromDate, toDate, trainerName, typeId);
    }

    public List<Training> getTrainerTrainings(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        log.info("Getting trainer trainings username={}", username);

        trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        return trainingRepository.findTrainerTrainings(
                username, fromDate, toDate, traineeName);
    }

    public Training getById(Long id) {
        log.debug("Fetching training by id={}", id);
        return trainingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Training not found id={}", id);
                    return new TrainingNotFoundException(id);
                });
    }

    public List<Training> getAll() {
        log.debug("Fetching all trainings");
        return trainingRepository.findAll();
    }

    private void validateTraining(Training training) {
        ValidationUtil.notBlank(training.getName(), "Training name");
        ValidationUtil.notNull(training.getDate(), "Training date");
        ValidationUtil.positive(training.getDuration(), "Training duration");

        ValidationUtil.notNull(training.getTrainer(), "Trainer");
        ValidationUtil.notNull(training.getTrainer().getId(), "Trainer ID");

        ValidationUtil.notNull(training.getTrainee(), "Trainee");
        ValidationUtil.notNull(training.getTrainee().getId(), "Trainee ID");

        ValidationUtil.notNull(training.getType(), "Training type");
    }
}
