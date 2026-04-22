package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.model.Trainee;
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
    private final TraineeDao dao;
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

        Trainee saved = dao.save(trainee);
        log.info("Trainee created successfully with id={}", saved.getId());

        return saved;
    }

    public Trainee update(Trainee trainee) {
        log.info("Updating trainee with id={}", trainee.getId());

        Trainee existingTrainee = getById(trainee.getId());

        UserUpdateUtil.updateNameFields(existingTrainee, trainee.getFirstName(), trainee.getLastName(), usernameGenerator);

        existingTrainee.setAddress(trainee.getAddress());
        existingTrainee.setDateOfBirth(trainee.getDateOfBirth());

        Trainee updated = dao.save(existingTrainee);

        log.info("Trainee updated successfully id={}", updated.getId());

        return updated;
    }

    public Trainee getById(Long id) {
        log.debug("Fetching trainee by id={}", id);

        return dao.findTraineeById(id)
                .orElseThrow(() -> {
                    log.warn("Trainee not found id={}", id);
                    return new TraineeNotFoundException(id);
                });
    }

    public List<Trainee> getAll() {
        log.debug("Fetching all trainees");
        return dao.findAllTrainees();
    }

    public void deleteById(Long id) {
        log.info("Deleting trainee id={}", id);
        dao.deleteTraineeById(id);
    }
}
