package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeService {
    private final TraineeRepository repository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Transactional
    public Trainee create(Trainee trainee) {
        log.info("Creating trainee: {} {}", trainee.getFirstName(), trainee.getLastName());

        String username = usernameGenerator.generate(trainee.getFirstName(), trainee.getLastName());
        String password = passwordGenerator.generate();

        log.debug("Generated username={}", username);

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        Trainee saved = repository.save(trainee);
        log.info("Trainee created successfully with id={}", saved.getId());

        return saved;
    }

    @Transactional
    public Trainee update(Trainee trainee) {
        log.info("Updating trainee with id={}", trainee.getId());

        Trainee existingTrainee = repository.findById(trainee.getId())
                .orElseThrow(() -> {
                    log.warn("Trainee not found id={}", trainee.getId());
                    return new TraineeNotFoundException(trainee.getUsername());
                });

        UserUpdateUtil.updateNameFields(existingTrainee, trainee.getFirstName(), trainee.getLastName(), usernameGenerator);

        existingTrainee.setAddress(trainee.getAddress());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());

        Trainee updated = repository.save(existingTrainee);

        log.info("Trainee updated successfully id={}", updated.getId());

        return updated;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        log.info("Changing password for trainee username={}", username);

        Trainee trainee = repository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        trainee.setPassword(newPassword);

        repository.save(trainee);

        log.info("Password changed successfully for username={}", username);
    }

    @Transactional
    public void activate(String username) {
        log.info("Activating trainee username={}", username);

        Trainee trainee = repository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        if (trainee.getActive()) {
            throw new IllegalStateException("Trainee already active");
        }

        trainee.setActive(true);
        repository.save(trainee);

        log.info("Trainee activated username={}", username);
    }

    @Transactional
    public void deactivate(String username) {
        log.info("Deactivating trainee username={}", username);

        Trainee trainee = repository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        if (!trainee.getActive()) {
            throw new IllegalStateException("Trainee already inactive");
        }

        trainee.setActive(false);
        repository.save(trainee);

        log.info("Trainee deactivated username={}", username);
    }

    public Trainee getByUsername(String username) {
        log.debug("Fetching trainee by username={}", username);

        return repository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found username={}", username);
                    return new TraineeNotFoundException(username);
                });
    }

    public List<Trainee> getAll() {
        log.debug("Fetching all trainees");
        return repository.findAll();
    }

    @Transactional
    public void deleteByUsername(String username) {
        log.info("Deleting trainee username={}", username);
        repository.deleteByUsername(username);
    }
}
