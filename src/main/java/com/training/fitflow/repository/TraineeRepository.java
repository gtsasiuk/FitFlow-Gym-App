package com.training.fitflow.repository;

import com.training.fitflow.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUsername(String username);

    Optional<Trainee> deleteByUsername(String username);
}
