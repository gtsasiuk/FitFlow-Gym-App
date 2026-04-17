package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TraineeService {
    private final TraineeDao dao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;


    public Trainee create(Trainee trainee) {
        String username = usernameGenerator.generate(trainee.getFirstName(), trainee.getLastName());
        String password = passwordGenerator.generate();

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
}
