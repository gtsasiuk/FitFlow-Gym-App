package com.training.fitflow.dao;

import com.training.fitflow.model.Training;
import com.training.fitflow.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Training save(Training training) {
        storage.getTrainings().put(training.getId(), training);
        return training;
    }

    public Optional<Training> getTrainingById(Long id) {
        return Optional.ofNullable(storage.getTrainings().get(id));
    }

    public List<Training> getAllTrainings() {
        return storage.getTrainings().values().stream().toList();
    }
}
