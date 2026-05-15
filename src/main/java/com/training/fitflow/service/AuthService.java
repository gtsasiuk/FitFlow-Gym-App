package com.training.fitflow.service;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.util.ValidationUtil;
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

    private void validateCredentials(String username, String password) {
        ValidationUtil.notBlank(username, "Username");
        ValidationUtil.notBlank(password, "Password");
    }

    public void authenticate(String username, String password) {
        validateCredentials(username, password);

        log.info("Authenticating user with username={}", username);
        Optional<Trainee> traineeOpt = traineeRepository.findByUsername(username);

        if (traineeOpt.isPresent()) {
            Trainee trainee = traineeOpt.get();

            if (!trainee.getPassword().equals(password)) {
                log.warn("Invalid password for trainee username={}", username);
                throw new BadCredentialException(username);
            }

            if (!trainee.getActive()) {
                throw new UserDeactivatedException(username);
            }
            log.info("Authenticating trainee with username={}", username);
            return;
        }

        Optional<Trainer> trainerOpt = trainerRepository.findByUsername(username);

        if (trainerOpt.isPresent()) {
            Trainer trainer = trainerOpt.get();

            if (!trainer.getPassword().equals(password)) {
                log.warn("Invalid password for trainer username={}", username);
                throw new BadCredentialException(username);
            }

            if (!trainer.getActive()) {
                throw new UserDeactivatedException(username);
            }
            log.info("Authenticating trainer with username={}", username);
            return;
        }

        throw new BadCredentialException(username);
    }
}
