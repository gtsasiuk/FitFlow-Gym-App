package com.training.fitflow.service;

import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerService {
    private final TrainerDao dao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public Trainer create(Trainer trainer) {
        String username = usernameGenerator.generate(trainer.getFirstName(), trainer.getLastName());
        String password = passwordGenerator.generate();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        return dao.save(trainer);
    }

    public Trainer update(Trainer trainer) {
        Trainer existingTrainer = getById(trainer.getId());
        UserUpdateUtil.updateNameFields(existingTrainer, trainer.getFirstName(), trainer.getLastName(), usernameGenerator);
        existingTrainer.setSpecialization(trainer.getSpecialization());
        return dao.save(existingTrainer);
    }

    public Trainer getById(Long id) {
        return dao.findTrainerById(id).orElseThrow(() -> new RuntimeException("Trainer not found"));
    }

    public List<Trainer> getAll() {
        return dao.findAllTrainers();
    }
}
