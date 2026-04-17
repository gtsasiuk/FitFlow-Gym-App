package com.training.fitflow.dao;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainer save(Trainer trainer) {
        storage.getTrainers().put(trainer.getId(), trainer);
        return trainer;
    }

    public Optional<Trainer> getTrainerById(Long id) {
        return Optional.ofNullable(storage.getTrainers().get(id));
    }

    public List<Trainer> getAllTrainers() {
        return storage.getTrainers().values().stream().toList();
    }
}

