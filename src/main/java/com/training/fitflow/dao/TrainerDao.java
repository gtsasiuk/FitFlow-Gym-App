package com.training.fitflow.dao;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Trainee trainer) {
        storage.getTrainers().put(trainer.getId(), trainer);
    }

    public Trainee getTrainerById(Long id) {
        return (Trainee) storage.getTrainers().get(id);
    }

    public List<Trainer> getAllTrainers() {
        return storage.getTrainers().values().stream()
                .map(trainer -> (Trainer) trainer)
                .toList();
    }
}

