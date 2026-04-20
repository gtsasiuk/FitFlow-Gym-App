package com.training.fitflow.service;

import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    @Mock
    private TrainerDao dao;

    @Mock
    private UsernameGenerator usernameGenerator;

    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService service;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer(
                1L,
                "John",
                "Doe",
                null,
                null,
                null,
                TrainingType.YOGA
        );
    }

    @Test
    @DisplayName("Create → should generate username, password and save trainer")
    void create_shouldGenerateUsernamePasswordAndSaveTrainer() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("password123");
        when(dao.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = service.create(trainer);

        assertAll(
                () -> assertEquals("John.Doe", result.getUsername()),
                () -> assertEquals("password123", result.getPassword()),
                () -> assertTrue(result.getActive())
        );

        verify(dao).save(trainer);
    }

    @Test
    @DisplayName("Update → should update specialization without changing username if name is same")
    void update_shouldUpdateSpecializationWithoutChangingUsernameIfNameSame() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true,
                TrainingType.YOGA
        );

        when(dao.findTrainerById(1L)).thenReturn(Optional.of(existing));
        when(dao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setSpecialization(TrainingType.YOGA);

        Trainer result = service.update(trainer);

        assertAll(
                () -> assertEquals("John.Doe", result.getUsername()),
                () -> assertEquals(TrainingType.YOGA, result.getSpecialization())
        );
    }

    @Test
    @DisplayName("Update → should regenerate username if name changed")
    void update_shouldRegenerateUsernameIfNameChanged() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true,
                TrainingType.YOGA
        );

        when(dao.findTrainerById(1L)).thenReturn(Optional.of(existing));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(dao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        Trainer result = service.update(trainer);

        assertEquals("Jane.Smith", result.getUsername());
    }

    @Test
    @DisplayName("GetById → should return trainer when exists")
    void getById_shouldReturnTrainer_whenExists() {
        when(dao.findTrainerById(1L)).thenReturn(Optional.of(trainer));

        Trainer result = service.getById(1L);

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("GetById → should throw exception when trainer not found")
    void getById_shouldThrowException_whenNotFound() {
        when(dao.findTrainerById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getById(1L));

        assertEquals("Trainer not found", ex.getMessage());
    }

    @Test
    @DisplayName("GetAll → should return list of trainers")
    void getAll_shouldReturnAllTrainers() {
        when(dao.findAllTrainers()).thenReturn(List.of(trainer));

        List<Trainer> result = service.getAll();

        assertEquals(1, result.size());
        verify(dao).findAllTrainers();
    }

    @Test
    @DisplayName("Trainer.toString() → should contain all main fields")
    void toString_shouldContainAllFields() {
        Trainer trainer = new Trainer(
                1L,
                "John",
                "Doe",
                "John.Doe",
                "password",
                true,
                TrainingType.YOGA
        );

        String result = trainer.toString();

        assertAll(
                () -> assertTrue(result.contains("John")),
                () -> assertTrue(result.contains("Doe")),
                () -> assertTrue(result.contains("John.Doe")),
                () -> assertTrue(result.contains("password")),
                () -> assertTrue(result.contains("Yoga"))
        );
    }
}