package com.training.fitflow.dao;

import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TrainerDao Tests")
class TrainerDaoTest {
    private TrainerDao dao;
    private InMemoryStorage storage;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        dao = new TrainerDao();
        dao.setStorage(storage);

        trainer = new Trainer(
                1L,
                "John",
                "Doe",
                "John.Doe",
                "password",
                true,
                TrainingType.YOGA
        );
    }

    @Test
    @DisplayName("Save → should store trainer in memory")
    void save_shouldStoreTrainer() {
        Trainer result = dao.save(trainer);

        assertEquals(trainer, result);
        assertEquals(1, storage.getTrainers().size());
        assertTrue(storage.getTrainers().containsKey(1L));
    }

    @Test
    @DisplayName("FindById → should return trainer when exists")
    void findById_shouldReturnTrainer_whenExists() {
        dao.save(trainer);

        Optional<Trainer> result = dao.findTrainerById(1L);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    @DisplayName("FindById → should return empty when trainer does not exist")
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainer> result = dao.findTrainerById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("FindAll → should return all trainers")
    void findAll_shouldReturnAllTrainers() {
        dao.save(trainer);

        List<Trainer> result = dao.findAllTrainers();

        assertEquals(1, result.size());
        assertEquals(trainer, result.get(0));
    }

    @Test
    @DisplayName("Save → should overwrite trainer with same ID")
    void save_shouldOverwriteTrainer() {
        dao.save(trainer);

        Trainer updated = new Trainer(
                1L,
                "Jane",
                "Smith",
                "Jane.Smith",
                "newPass",
                true,
                TrainingType.STRETCHING
        );

        dao.save(updated);

        Trainer result = dao.findTrainerById(1L).orElseThrow();

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(TrainingType.STRETCHING, result.getSpecialization());
    }

    @Test
    @DisplayName("FindAll → should return empty list when no trainers")
    void findAll_shouldReturnEmptyList() {
        List<Trainer> result = dao.findAllTrainers();

        assertTrue(result.isEmpty());
    }
}