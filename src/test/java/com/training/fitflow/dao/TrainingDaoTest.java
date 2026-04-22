package com.training.fitflow.dao;

import com.training.fitflow.model.Training;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrainingDao Tests")
class TrainingDaoTest {
    private TrainingDao dao;
    private InMemoryStorage storage;

    private Training training;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        dao = new TrainingDao();
        dao.setStorage(storage);

        training = new Training(
                1L,
                10L,
                20L,
                "Morning Workout",
                TrainingType.FITNESS,
                LocalDate.of(2025, 1, 1),
                60
        );
    }

    @Test
    @DisplayName("Save → should store and return training")
    void save_shouldStoreTraining() {
        Training result = dao.save(training);

        assertEquals(training, result);

        Optional<Training> found = dao.findTrainingById(1L);
        assertTrue(found.isPresent());
        assertEquals(training, found.get());
    }

    @Test
    @DisplayName("FindById → should return training when exists")
    void findById_shouldReturnTraining_whenExists() {
        dao.save(training);

        Optional<Training> result = dao.findTrainingById(1L);

        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    @DisplayName("FindById → should return empty when training does not exist")
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Training> result = dao.findTrainingById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("FindAll → should return all trainings")
    void findAll_shouldReturnAllTrainings() {
        dao.save(training);

        List<Training> result = dao.findAllTrainings();

        assertEquals(1, result.size());
        assertEquals(training, result.get(0));
    }

    @Test
    @DisplayName("FindAll → should return empty list when no trainings")
    void findAll_shouldReturnEmptyList() {
        List<Training> result = dao.findAllTrainings();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Save → should overwrite training with same ID")
    void save_shouldOverwriteTraining() {
        dao.save(training);

        Training updated = new Training(
                1L,
                11L,
                22L,
                "Evening Workout",
                TrainingType.YOGA,
                LocalDate.of(2025, 1, 2),
                90
        );

        dao.save(updated);

        Training result = dao.findTrainingById(1L).orElseThrow();

        assertEquals("Evening Workout", result.getName());
        assertEquals(90, result.getDuration());
        assertEquals(TrainingType.YOGA, result.getType());
    }
}