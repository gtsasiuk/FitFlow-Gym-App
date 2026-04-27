package com.training.fitflow.service;

import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import com.training.fitflow.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
    private final TrainerRepository repository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    private void validateTrainerForCreate(Trainer trainer) {
        ValidationUtil.notNull(trainer, "Trainer");

        ValidationUtil.notBlank(trainer.getFirstName(), "First name");
        ValidationUtil.notBlank(trainer.getLastName(), "Last name");

        ValidationUtil.notNull(trainer.getSpecialization(), "Specialization");
    }

    private void validateTrainerForUpdate(Trainer trainer) {
        ValidationUtil.notNull(trainer.getId(), "Trainer ID");

        ValidationUtil.notBlank(trainer.getFirstName(), "First name");
        ValidationUtil.notBlank(trainer.getLastName(), "Last name");

        ValidationUtil.notNull(trainer.getSpecialization(), "Specialization");
    }

    private void validateTrainerForNewPassword(String username, String newPassword) {
        ValidationUtil.notBlank(username, "Username");
        ValidationUtil.notBlank(newPassword, "New password");
    }

    @Transactional
    public Trainer create(Trainer trainer) {
        log.info("Creating trainer: {} {}", trainer.getFirstName(), trainer.getLastName());

        validateTrainerForCreate(trainer);

        String username = usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        String password = passwordGenerator.generate();

        log.debug("Generated username={}", username);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);
        trainer.setSpecialization(trainer.getSpecialization());

        Trainer saved = repository.save(trainer);

        log.info("Trainer created successfully with id={}", saved.getId());

        return saved;
    }

    @Transactional
    public Trainer update(Trainer trainer) {
        log.info("Updating trainer id={}", trainer.getId());

        validateTrainerForUpdate(trainer);

        Trainer existingTrainer = repository.findById(trainer.getId())
                .orElseThrow(() -> {
                    log.warn("Trainer not found id={}", trainer.getId());
                    return new TrainerNotFoundException(trainer.getUsername());
                });

        UserUpdateUtil.updateNameFields(existingTrainer, trainer.getFirstName(), trainer.getLastName(), usernameGenerator);
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updated = repository.save(existingTrainer);

        log.info("Trainer updated successfully id={}", updated.getId());

        return updated;
    }

    @Transactional
    public void changePassword(String username, String newPassword) {
        validateTrainerForNewPassword(username, newPassword);
        log.info("Changing password for trainer username={}", username);

        Trainer trainer = repository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        trainer.setPassword(newPassword);

        repository.save(trainer);

        log.info("Password changed successfully for username={}", username);
    }

    @Transactional
    public void activate(String username) {
        log.info("Activating trainer username={}", username);

        Trainer trainer = repository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        if (trainer.getActive()) {
            throw new IllegalStateException("Trainer already active");
        }

        trainer.setActive(true);
        repository.save(trainer);

        log.info("Trainer activated username={}", username);
    }

    @Transactional
    public void deactivate(String username) {
        log.info("Deactivating trainer username={}", username);

        Trainer trainer = repository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        if (!trainer.getActive()) {
            throw new IllegalStateException("Trainer already inactive");
        }

        trainer.setActive(false);
        repository.save(trainer);

        log.info("Trainer deactivated username={}", username);
    }

    public Trainer getByUsername(String username) {
        log.debug("Fetching trainer by username={}", username);
        return repository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", username);
                    return new TrainerNotFoundException(username);
                });
    }

    public List<Trainer> getAll() {
        log.debug("Fetching all trainers");
        return repository.findAll();
    }
}
