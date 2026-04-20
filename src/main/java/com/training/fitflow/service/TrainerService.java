package com.training.fitflow.service;

import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.model.Trainer;
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
public class TrainerService {
    private final TrainerDao dao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public Trainer create(Trainer trainer) {
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        String username = usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        String password = passwordGenerator.generate();

        log.debug("Generated username={}", username);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        Trainer saved = dao.save(trainer);

        log.info("Trainer created successfully with id={}", saved.getId());

        return saved;
    }

    public Trainer update(Trainer trainer) {
        log.info("Updating trainer id={}", trainer.getId());

        Trainer existingTrainer = getById(trainer.getId());
        UserUpdateUtil.updateNameFields(existingTrainer, trainer.getFirstName(), trainer.getLastName(), usernameGenerator);
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updated = dao.save(existingTrainer);

        log.info("Trainer updated successfully id={}", updated.getId());

        return updated;
    }

    public Trainer getById(Long id) {
        log.debug("Fetching trainer by id={}", id);
        return dao.findTrainerById(id)
                .orElseThrow(() -> {
                    log.warn("Trainer not found id={}", id);
                    return new RuntimeException("Trainer not found");
                });
    }

    public List<Trainer> getAll() {
        log.debug("Fetching all trainers");
        return dao.findAllTrainers();
    }
}
