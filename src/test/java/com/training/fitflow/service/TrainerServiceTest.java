package com.training.fitflow.service;

import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {
    @Mock
    private TrainerRepository repository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerService service;

    private Trainer trainer;
    private TrainingType type;

    @BeforeEach
    void setUp() {
        type = new TrainingType(1L, "Yoga");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setSpecialization(type);
        trainer.setActive(false);
    }

    @Test
    @DisplayName("Create → should generate username, password and save trainer")
    void create_shouldGenerateUsernamePasswordAndSaveTrainer() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("password123");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = service.create(trainer);

        assertAll(
                () -> assertEquals("John.Doe", result.getUsername()),
                () -> assertEquals("password123", result.getPassword()),
                () -> assertTrue(result.getActive())
        );

        verify(repository).save(trainer);
    }

    @Test
    @DisplayName("Update → should update specialization without changing username if name same")
    void update_shouldUpdateWithoutChangingUsername() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true, type
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        Trainer result = service.update(trainer);

        assertEquals("John.Doe", result.getUsername());
        assertEquals(type, result.getSpecialization());
    }

    @Test
    @DisplayName("Update → should regenerate username if name changed")
    void update_shouldRegenerateUsername() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true, type
        );

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");

        Trainer result = service.update(trainer);

        assertEquals("Jane.Smith", result.getUsername());
    }

    @Test
    @DisplayName("Update → should throw exception when trainer not found")
    void update_shouldThrowException_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.update(trainer)
        );
    }

    @Test
    @DisplayName("ChangePassword → should update password")
    void changePassword_shouldUpdatePassword() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "old", true, type
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));

        service.changePassword("John.Doe", "newPass");

        assertEquals("newPass", existing.getPassword());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("ChangePassword → should throw when trainer not found")
    void changePassword_shouldThrow_whenNotFound() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.changePassword("John.Doe", "newPass")
        );
    }

    @Test
    @DisplayName("Activate → should activate trainer")
    void activate_shouldActivateTrainer() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", false, type
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));

        service.activate("John.Doe");

        assertTrue(existing.getActive());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Activate → should throw if already active")
    void activate_shouldThrowIfAlreadyActive() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true, type
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> service.activate("John.Doe"));
    }

    @Test
    @DisplayName("Deactivate → should deactivate trainer")
    void deactivate_shouldDeactivateTrainer() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", true, type
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));

        service.deactivate("John.Doe");

        assertFalse(existing.getActive());
        verify(repository).save(existing);
    }

    @Test
    @DisplayName("Deactivate → should throw if already inactive")
    void deactivate_shouldThrowIfAlreadyInactive() {
        Trainer existing = new Trainer(
                1L, "John", "Doe", "John.Doe", "pass", false, type
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> service.deactivate("John.Doe"));
    }

    @Test
    @DisplayName("GetByUsername → should return trainer when exists")
    void getByUsername_shouldReturnTrainer() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(trainer));

        Trainer result = service.getByUsername("John.Doe");

        assertEquals(trainer, result);
    }

    @Test
    @DisplayName("GetByUsername → should throw when not found")
    void getByUsername_shouldThrow_whenNotFound() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> service.getByUsername("John.Doe"));
    }

    @Test
    @DisplayName("GetAll → should return all trainers")
    void getAll_shouldReturnAll() {
        when(repository.findAll()).thenReturn(List.of(trainer));

        List<Trainer> result = service.getAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("toString → should contain main fields")
    void toString_shouldContainFields() {
        Trainer t = new Trainer(
                1L,
                "John",
                "Doe",
                "John.Doe",
                "password",
                true,
                type
        );

        String result = t.toString();

        assertAll(
                () -> assertTrue(result.contains("John")),
                () -> assertTrue(result.contains("Doe")),
                () -> assertTrue(result.contains("John.Doe")),
                () -> assertTrue(result.contains("password"))
        );
    }
}