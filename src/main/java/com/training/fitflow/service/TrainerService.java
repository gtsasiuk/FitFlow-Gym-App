package com.training.fitflow.service;

import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.mapper.TrainerMapper;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingTypeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import com.training.fitflow.util.ValidationUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerMapper trainerMapper;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

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
    public TrainerCreateResponse create(TrainerCreateRequest request) {
        log.info("Creating trainer: {} {}", request.firstName(), request.lastName());
        TrainingType specialization = trainingTypeRepository.findById(request.specializationId())
                .orElseThrow(() -> new EntityNotFoundException("Training type not found"));

        Trainer trainer = trainerMapper.toEntity(request);
        trainer.setSpecialization(specialization);

        String username = usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        String password = passwordGenerator.generate();

        log.debug("Generated username={}", username);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        Trainer saved = trainerRepository.save(trainer);
        log.info("Trainer created successfully with id={}", saved.getId());

        return trainerMapper.toCreateResponse(saved);
    }

    @Transactional
    public Trainer update(Trainer trainer) {
        log.info("Updating trainer id={}", trainer.getId());

        validateTrainerForUpdate(trainer);

        Trainer existingTrainer = trainerRepository.findById(trainer.getId())
                .orElseThrow(() -> {
                    log.warn("Trainer not found id={}", trainer.getId());
                    return new TrainerNotFoundException(trainer.getUsername());
                });

        UserUpdateUtil.updateNameFields(existingTrainer, trainer.getFirstName(), trainer.getLastName(), usernameGenerator);
        existingTrainer.setSpecialization(trainer.getSpecialization());

        Trainer updated = trainerRepository.save(existingTrainer);

        log.info("Trainer updated successfully id={}", updated.getId());

        return updated;
    }

    @Transactional
    public void activate(String username) {
        log.info("Activating trainer username={}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        if (trainer.getActive()) {
            throw new IllegalStateException("Trainer already active");
        }

        trainer.setActive(true);
        trainerRepository.save(trainer);

        log.info("Trainer activated username={}", username);
    }

    @Transactional
    public void deactivate(String username) {
        log.info("Deactivating trainer username={}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        if (!trainer.getActive()) {
            throw new IllegalStateException("Trainer already inactive");
        }

        trainer.setActive(false);
        trainerRepository.save(trainer);

        log.info("Trainer deactivated username={}", username);
    }

    public Trainer getByUsername(String username) {
        log.debug("Fetching trainer by username={}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", username);
                    return new TrainerNotFoundException(username);
                });
    }

    public List<Trainer> getAll() {
        log.debug("Fetching all trainers");
        return trainerRepository.findAll();
    }
}
