package com.training.fitflow.dao;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraineeDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public void save(Trainee trainee) {
        storage.getTrainees().put(trainee.getId(), trainee);
    }

    public List<Trainee> getAllTrainees() {
        return storage.getTrainees().values().stream().toList();
    }

    public Trainee getTraineeById(Long id) {
        return storage.getTrainees().get(id);
    }

    public void deleteTraineeById(Long id) {
        storage.getTrainees().remove(id);
    }
}
