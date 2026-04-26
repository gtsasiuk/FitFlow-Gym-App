package com.training.fitflow.service;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public Trainee authenticateTrainee(String username, String password) {
        log.info("Authenticating trainee with username={}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        if (!trainee.getPassword().equals(password)) {
            log.warn("Invalid password for trainee username={}", username);
            throw new BadCredentialException(username);
        }

        if (!trainee.getActive()) {
            throw new UserDeactivatedException(username);
        }

        return trainee;
    }

    public Trainer authenticateTrainer(String username, String password) {
        log.info("Authenticating trainer with username={}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        if (!trainer.getPassword().equals(password)) {
            log.warn("Invalid password for trainer username={}", username);
            throw new BadCredentialException(username);
        }

        if (!trainer.getActive()) {
            throw new UserDeactivatedException(username);
        }

        return trainer;
    }
}
