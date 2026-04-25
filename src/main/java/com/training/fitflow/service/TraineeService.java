package com.training.fitflow.service;


import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
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

    public Trainee update(Trainee trainee) {
        log.info("Updating trainee with id={}", trainee.getId());

        Trainee existingTrainee = getByUsername(trainee.getUsername());

        UserUpdateUtil.updateNameFields(existingTrainee, trainee.getFirstName(), trainee.getLastName(), usernameGenerator);

        existingTrainee.setAddress(trainee.getAddress());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());

        Trainee updated = repository.save(existingTrainee);

        log.info("Trainee updated successfully id={}", updated.getId());

        return updated;
    }

    public Trainee getByUsername(String username) {
        log.debug("Fetching trainee by username={}", username);

        return repository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found id={}", username);
                    return new TraineeNotFoundException(username);
                });
    }

    public List<Trainee> getAll() {
        log.debug("Fetching all trainees");
        return repository.findAll();
    }

    public void deleteByUsername(String username) {
        log.info("Deleting trainee username={}", username);
        repository.deleteByUsername(username);
    }
}
