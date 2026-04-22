package com.training.fitflow.dao;

import com.training.fitflow.model.Trainee;
import com.training.fitflow.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraineeDao Tests")
class TraineeDaoTest {

    private TraineeDao dao;
    private InMemoryStorage storage;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
        dao = new TraineeDao();
        dao.setStorage(storage);

        trainee = new Trainee(
                1L,
                "John",
                "Doe",
                "John.Doe",
                "password",
                true,
                LocalDate.of(2000, 1, 1),
                "Address"
        );
    }

    @Test
    @DisplayName("Save → should store trainee in memory")
    void save_shouldStoreTrainee() {
        Trainee result = dao.save(trainee);

        assertEquals(trainee, result);
        assertEquals(1, storage.getTrainees().size());
        assertTrue(storage.getTrainees().containsKey(1L));
    }

    @Test
    @DisplayName("FindAll → should return all stored trainees")
    void findAll_shouldReturnAllTrainees() {
        dao.save(trainee);

        List<Trainee> result = dao.findAllTrainees();

        assertEquals(1, result.size());
        assertEquals(trainee, result.get(0));
    }

    @Test
    @DisplayName("FindById → should return trainee when exists")
    void findById_shouldReturnTrainee_whenExists() {
        dao.save(trainee);

        Optional<Trainee> result = dao.findTraineeById(1L);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    @DisplayName("FindById → should return empty when not exists")
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Trainee> result = dao.findTraineeById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DeleteById → should remove trainee from storage")
    void deleteById_shouldRemoveTrainee() {
        dao.save(trainee);

        dao.deleteTraineeById(1L);

        assertFalse(storage.getTrainees().containsKey(1L));
        assertTrue(storage.getTrainees().isEmpty());
    }

    @Test
    @DisplayName("Save → should overwrite existing trainee with same ID")
    void save_shouldOverwriteExistingTrainee() {
        dao.save(trainee);

        Trainee updated = new Trainee(
                1L,
                "Jane",
                "Smith",
                "Jane.Smith",
                "newPass",
                true,
                LocalDate.of(1995, 5, 5),
                "New Address"
        );

        dao.save(updated);

        Trainee result = dao.findTraineeById(1L).orElseThrow();

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("New Address", result.getAddress());
    }
}