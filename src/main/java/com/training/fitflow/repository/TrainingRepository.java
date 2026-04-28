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
    @Query("""
        SELECT t FROM Training t
        WHERE t.trainee.username = :username
        AND (:fromDate IS NULL OR t.date >= :fromDate)
        AND (:toDate IS NULL OR t.date <= :toDate)
        AND (:trainerName IS NULL OR :trainerName = '' OR
            LOWER(t.trainer.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%'))
            OR LOWER(t.trainer.lastName) LIKE LOWER(CONCAT('%', :trainerName, '%')))
        AND (:typeId IS NULL OR t.type.id = :typeId)
    """)
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("typeId") Long typeId
    );

    @Query("""
        SELECT t FROM Training t
        WHERE t.trainer.username = :username
        AND (:fromDate IS NULL OR t.date >= :fromDate)
        AND (:toDate IS NULL OR t.date <= :toDate)
        AND (:traineeName IS NULL OR :traineeName = '' OR
            LOWER(t.trainee.firstName) LIKE LOWER(CONCAT('%', :traineeName, '%'))
            OR LOWER(t.trainee.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%')))
    """)
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}
