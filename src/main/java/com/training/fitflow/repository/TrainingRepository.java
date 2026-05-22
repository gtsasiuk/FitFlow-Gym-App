package com.training.fitflow.repository;

import com.training.fitflow.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    @Query(value = """
        SELECT t.* FROM trainings t
        JOIN trainees tr ON tr.id = t.trainee_id
        JOIN users u ON u.id = tr.id
        JOIN trainers trn ON trn.id = t.trainer_id
        JOIN users u2 ON u2.id = trn.id
        WHERE u.username = :username
        AND (CAST(:fromDate AS date) IS NULL OR t.training_date >= CAST(:fromDate AS date))
        AND (CAST(:toDate AS date) IS NULL OR t.training_date <= CAST(:toDate AS date))
        AND (CAST(:trainerName AS text) IS NULL OR (
            LOWER(u2.first_name) LIKE LOWER(CONCAT('%', :trainerName, '%'))
            OR LOWER(u2.last_name) LIKE LOWER(CONCAT('%', :trainerName, '%'))
        ))
        AND (CAST(:typeId AS bigint) IS NULL OR t.training_type_id = CAST(:typeId AS bigint))
    """, nativeQuery = true)
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("typeId") Long typeId
    );

    @Query(value = """
        SELECT t.* FROM trainings t
        JOIN trainers trn ON trn.id = t.trainer_id
        JOIN users u ON u.id = trn.id
        JOIN trainees tr ON tr.id = t.trainee_id
        JOIN users u2 ON u2.id = tr.id
        WHERE u.username = :username
        AND (CAST(:fromDate AS date) IS NULL OR t.training_date >= CAST(:fromDate AS date))
        AND (CAST(:toDate AS date) IS NULL OR t.training_date <= CAST(:toDate AS date))
        AND (CAST(:traineeName AS text) IS NULL OR (
            LOWER(u2.first_name) LIKE LOWER(CONCAT('%', :traineeName, '%'))
            OR LOWER(u2.last_name) LIKE LOWER(CONCAT('%', :traineeName, '%'))
        ))
    """, nativeQuery = true)
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}
