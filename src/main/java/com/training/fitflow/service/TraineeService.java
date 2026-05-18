package com.training.fitflow.service;

import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.mapper.TraineeMapper;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import com.training.fitflow.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeMapper traineeMapper;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    private void validateTraineeForUpdate(Trainee trainee) {
        ValidationUtil.notNull(trainee.getId(), "Trainee ID");

        ValidationUtil.notBlank(trainee.getFirstName(), "First name");
        ValidationUtil.notBlank(trainee.getLastName(), "Last name");
    }

    private void validateTraineeForTrainersUpdate(String username, List<Long> trainerIds) {
        ValidationUtil.notBlank(username, "Username");
        ValidationUtil.notNull(trainerIds, "Trainer IDs list");
    }

    @Transactional
    public TraineeCreateResponse create(TraineeCreateRequest request) {
        log.info("Creating trainee: {} {}", request.firstName(), request.lastName());

        Trainee trainee = traineeMapper.toEntity(request);

        String username = usernameGenerator.generate(trainee.getFirstName(), trainee.getLastName());
        String password = passwordGenerator.generate();

        log.debug("Generated username={}", username);

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        Trainee saved = traineeRepository.save(trainee);
        log.info("Trainee created successfully with id={}", saved.getId());

        return traineeMapper.toTraineeCreateResponse(saved);
    }

    @Transactional
    public Trainee update(Trainee trainee) {
        log.info("Updating trainee with id={}", trainee.getId());

        validateTraineeForUpdate(trainee);

        Trainee existingTrainee = traineeRepository.findById(trainee.getId())
                .orElseThrow(() -> {
                    log.warn("Trainee not found id={}", trainee.getId());
                    return new TraineeNotFoundException(trainee.getUsername());
                });

        UserUpdateUtil.updateNameFields(existingTrainee, trainee.getFirstName(), trainee.getLastName(), usernameGenerator);

        existingTrainee.setAddress(trainee.getAddress());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());

        Trainee updated = traineeRepository.save(existingTrainee);

        log.info("Trainee updated successfully id={}", updated.getId());

        return updated;
    }

    @Transactional
    public void activate(String username) {
        log.info("Activating trainee username={}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        if (trainee.getActive()) {
            throw new IllegalStateException("Trainee already active");
        }

        trainee.setActive(true);
        traineeRepository.save(trainee);

        log.info("Trainee activated username={}", username);
    }

    @Transactional
    public void deactivate(String username) {
        log.info("Deactivating trainee username={}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        if (!trainee.getActive()) {
            throw new IllegalStateException("Trainee already inactive");
        }

        trainee.setActive(false);
        traineeRepository.save(trainee);

        log.info("Trainee deactivated username={}", username);
    }

    @Transactional
    public TraineeProfileResponse getByUsername(String username) {
        log.debug("Fetching trainee by username={}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found username={}", username);
                    return new TraineeNotFoundException(username);
                });
        return traineeMapper.toProfileResponse(trainee);
    }

    @Transactional
    public void updateTraineeTrainers(String username, List<Long> trainerIds) {
        validateTraineeForTrainersUpdate(username, trainerIds);
        log.info("Updating trainee trainers list username={}, trainerIds={}", username, trainerIds);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found when updating trainers list username={}", username);
                    return new TraineeNotFoundException(username);
                });

        Set<Trainer> newTrainers = new HashSet<>(
                trainerRepository.findAllById(trainerIds)
        );

        log.debug("Fetched trainers for assignment username={}, found={}/{}",
                username, newTrainers.size(), trainerIds.size());

        int beforeSize = trainee.getTrainers() != null ? trainee.getTrainers().size() : 0;

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(newTrainers);

        log.info("Trainee trainers updated successfully username={}, beforeCount={}, afterCount={}",
                username, beforeSize, newTrainers.size());
    }

    public List<Trainer> getUnassignedTrainers(String username) {
        log.debug("Fetching all unassigned trainers for trainee by username={}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        return trainerRepository.findNotAssignedToTrainee(trainee.getUsername());
    }

    public List<Trainee> getAll() {
        log.debug("Fetching all trainees");
        return traineeRepository.findAll();
    }

    @Transactional
    public void deleteByUsername(String username) {
        log.info("Deleting trainee username={}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        trainee.getTrainers().clear();
        traineeRepository.save(trainee);

        traineeRepository.delete(trainee);
        log.info("Trainee deleted username={}", username);
    }
}
