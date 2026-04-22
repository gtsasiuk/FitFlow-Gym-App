package com.training.fitflow.dao;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao {
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainee save(Trainee trainee) {
        storage.getTrainees().put(trainee.getId(), trainee);
        return trainee;
    }

    public List<Trainee> findAllTrainees() {
        return storage.getTrainees().values().stream().toList();
    }

    public Optional<Trainee> findTraineeById(Long id) {
        return Optional.ofNullable(storage.getTrainees().get(id));
    }

    public void deleteTraineeById(Long id) {
        storage.getTrainees().remove(id);
    }
}
