package com.training.fitflow.service;

import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.exception.SpecializationNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.mapper.TrainerMapper;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingTypeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
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

    @Transactional
    public TrainerCreateResponse create(TrainerCreateRequest request) {
        log.info("Creating trainer: {} {}", request.firstName(), request.lastName());
        TrainingType specialization = trainingTypeRepository.findById(request.specializationId())
                .orElseThrow(() -> new SpecializationNotFoundException("Training type not found"));

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
    public TrainerUpdateResponse update(String username, TrainerUpdateRequest request) {
        log.info("Updating trainer with username={}", username);

        Trainer existingTrainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", username);
                    return new TrainerNotFoundException(username);
                });

        existingTrainer.setFirstName(request.firstName());
        existingTrainer.setLastName(request.lastName());
        existingTrainer.setActive(request.isActive());

        Trainer updated = trainerRepository.save(existingTrainer);
        log.info("Trainer updated successfully id={}", updated.getId());

        return trainerMapper.toUpdateResponse(updated);
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

    @Transactional
    public TrainerProfileResponse getByUsername(String username) {
        log.debug("Fetching trainer by username={}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found username={}", username);
                    return new TrainerNotFoundException(username);
                });
        return trainerMapper.toProfileResponse(trainer);
    }

    public List<Trainer> getAll() {
        log.debug("Fetching all trainers");
        return trainerRepository.findAll();
    }
}
