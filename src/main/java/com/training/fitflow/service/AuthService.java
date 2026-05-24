package com.training.fitflow.service;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;

    public void authenticate(String username, String password) {
        log.info("Authenticating user with username={}", username);
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);

        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();

            if (!trainee.getPassword().equals(password)) {
                log.warn("Invalid password for trainee username={}", username);
                loginFailureCounter.increment();
                throw new BadCredentialException(username);
            }

            if (!trainee.getActive()) {
                loginFailureCounter.increment();
                throw new UserDeactivatedException(username);
            }
            log.info("Authenticating trainee with username={}", username);
            loginSuccessCounter.increment();
            return;
        }

        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);

        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();

            if (!trainer.getPassword().equals(password)) {
                log.warn("Invalid password for trainer username={}", username);
                loginFailureCounter.increment();
                throw new BadCredentialException(username);
            }

            if (!trainer.getActive()) {
                loginFailureCounter.increment();
                throw new UserDeactivatedException(username);
            }
            log.info("Authenticating trainer with username={}", username);
            loginSuccessCounter.increment();
            return;
        }

        throw new BadCredentialException(username);
    }

    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for username={}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);
        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();
            if (!trainee.getPassword().equals(oldPassword)) {
                log.warn("Invalid old password for username={}", username);
                throw new BadCredentialException(username);
            }
            trainee.setPassword(newPassword);
            traineeRepository.save(trainee);
            log.info("Password changed for username={}", username);
            return;
        }

        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);
        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();
            if (!trainer.getPassword().equals(oldPassword)) {
                log.warn("Invalid old password for username={}", username);
                throw new BadCredentialException(username);
            }
            trainer.setPassword(newPassword);
            trainerRepository.save(trainer);
            log.info("Password changed for username={}", username);
            return;
        }

        throw new BadCredentialException(username);
    }
}
