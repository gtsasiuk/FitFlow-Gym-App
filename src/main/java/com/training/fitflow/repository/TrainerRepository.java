package com.training.fitflow.repository;

import com.training.fitflow.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);

    @Query("""
        SELECT tr FROM Trainer tr
        WHERE tr.id NOT IN (
            SELECT t.id FROM Trainee ta
            JOIN ta.trainers t
            WHERE ta.username = :username
        )
    """)
    List<Trainer> findNotAssignedToTrainee(@Param("username") String username);
}
