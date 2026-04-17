package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.model.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeDao dao;

    public Trainee create(Trainee trainee) {
        String username = generateUsername(trainee.getFirstName(), trainee.getLastName());
        String password = generatePassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        return dao.save(trainee);
    }

    public Trainee update(Trainee trainee) {
        return dao.save(trainee);
    }

    public Trainee getById(Long id) {
        return dao.getTraineeById(id)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }

    public List<Trainee> getAll() {
        return dao.getAllTrainees();
    }

    public void deleteById(Long id) {
        dao.deleteTraineeById(id);
    }

    private String generateUsername(String firstName, String lastName) {
        String base = firstName + "." + lastName;

        List<Trainee> all = dao.getAllTrainees();

        long count = all.stream()
                .filter(t -> t.getUsername() != null && t.getUsername().startsWith(base))
                .count();

        return count == 0 ? base : base + count;
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

}
