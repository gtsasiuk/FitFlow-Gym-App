package com.training.fitflow.service;

import com.training.fitflow.client.WorkloadIntegrationService;
import com.training.fitflow.client.dto.TrainerWorkloadRequest;
import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.mapper.TrainingMapper;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
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
    private final TrainingMapper trainingMapper;
    private final WorkloadIntegrationService workloadIntegrationService;

    @Transactional
    public void create(TrainingCreateRequest request) {
        log.info("Creating training: name={}, trainerUsername={}, traineeUsername={}",
                request.trainingName(),
                request.trainerUsername(),
                request.traineeUsername()
        );
        Training training = trainingMapper.toEntity(request);

        Trainer trainer = trainerRepository.findByUsername(request.trainerUsername())
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", request.trainerUsername());
                    return new TrainerNotFoundException(request.trainerUsername());
                });

        Trainee trainee = traineeRepository.findByUsername(request.traineeUsername())
                .orElseThrow(() -> {
                    log.warn("Trainee not found username={}", request.traineeUsername());
                    return new TraineeNotFoundException(request.traineeUsername());
                });

        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(trainer.getSpecialization());

        Training saved = trainingRepository.save(training);

        log.info("About to send workload update for trainer={}", trainer.getUsername());
        try {
            workloadIntegrationService.sendWorkloadUpdate(
                    new TrainerWorkloadRequest(
                            trainer.getUsername(),
                            trainer.getFirstName(),
                            trainer.getLastName(),
                            trainer.getActive(),
                            saved.getDate(),
                            saved.getDuration().longValue(),
                            TrainerWorkloadRequest.ActionType.ADD
                    )
            );
        } catch (Exception ex) {
            log.error("Failed to notify workload service for trainer={}, training save is not affected. Reason: {}",
                    trainer.getUsername(), ex.getMessage());
        }
        log.info("Workload update call returned");

        log.info("Training created successfully with id={}", saved.getId());
    }

    public List<TraineeTrainingResponse> getTraineeTrainings(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            Long typeId
    ) {
        trainerName = (trainerName == null || trainerName.isBlank())
                ? null
                : trainerName;

        log.info("Getting trainee trainings username={}", username);

        traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        return trainingRepository.findTraineeTrainings(username, fromDate, toDate, trainerName, typeId)
                .stream()
                .map(trainingMapper::toTraineeTrainingResponse)
                .toList();
    }

    public List<TrainerTrainingResponse> getTrainerTrainings(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        log.info("Getting trainer trainings username={}", username);

        traineeName = (traineeName == null || traineeName.isBlank())
                ? null
                : traineeName;

        trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        return trainingRepository.findTrainerTrainings(username, fromDate, toDate, traineeName)
                .stream()
                .map(trainingMapper::toTrainerTrainingResponse)
                .toList();
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
