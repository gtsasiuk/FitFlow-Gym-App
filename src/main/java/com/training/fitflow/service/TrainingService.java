package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.model.Training;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        trainerRepository.findByUsername(training.getTrainer().getUsername())
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", training.getTrainer().getUsername());
                    return new TrainerNotFoundException(training.getTrainer().getUsername());
                });

        traineeRepository.findByUsername(training.getTrainee().getUsername())
                .orElseThrow(() -> {
                    log.warn("Trainee not found username={}", training.getTrainee().getUsername());
                    return new TraineeNotFoundException(training.getTrainee().getUsername());
                });

        Training saved = trainingRepository.save(training);

        log.info("Training created successfully with id={}", saved.getId());

        return saved;
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
}
