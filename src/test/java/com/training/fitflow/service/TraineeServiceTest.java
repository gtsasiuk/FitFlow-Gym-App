package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeService service;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee(
                1L,
                "John",
                "Doe",
                null,
                null,
                true,
                LocalDate.of(2000, 1, 1),
                "Address"
        );
    }

    @Test
    @DisplayName("Create → should generate username and password, then save")
    void create_shouldGenerateAndSave() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("pass123456");
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trainee result = service.create(trainee);

        assertEquals("John.Doe", result.getUsername());
        assertEquals("pass123456", result.getPassword());
        assertTrue(result.getActive());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Create → should throw when firstName is blank")
    void create_shouldThrow_whenFirstNameBlank() {
        trainee.setFirstName("");

        assertThrows(RuntimeException.class, () -> service.create(trainee));
    }

    @Test
    @DisplayName("Update → should update without changing username")
    void update_shouldKeepUsername() {
        Trainee existing = new Trainee(
                1L, "John", "Doe", "John.Doe", "pass", true,
                LocalDate.now(), "Old"
        );

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        trainee.setAddress("New");

        Trainee result = service.update(trainee);

        assertEquals("John.Doe", result.getUsername());
        assertEquals("New", result.getAddress());
    }

    @Test
    @DisplayName("Update → should regenerate username if name changed")
    void update_shouldRegenerateUsername() {
        Trainee existing = new Trainee(
                1L, "John", "Doe", "John.Doe", "pass", true,
                LocalDate.now(), "Old"
        );

        when(traineeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(traineeRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        trainee.setFirstName("Jane");
        trainee.setLastName("Smith");

        Trainee result = service.update(trainee);

        assertEquals("Jane.Smith", result.getUsername());
    }

    @Test
    @DisplayName("Update → should throw when trainee not found")
    void update_shouldThrow_whenNotFound() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.update(trainee));
    }


    @Test
    @DisplayName("ChangePassword → should update password")
    void changePassword_shouldUpdate() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.changePassword("John.Doe", "newPass");

        assertEquals("newPass", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("ChangePassword → should throw when user not found")
    void changePassword_shouldThrow() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> service.changePassword("John.Doe", "pass"));
    }


    @Test
    @DisplayName("Activate → should activate inactive trainee")
    void activate_shouldWork() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.activate("John.Doe");

        assertTrue(trainee.getActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Activate → should throw if already active")
    void activate_shouldThrow_whenAlreadyActive() {
        trainee.setActive(true);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class,
                () -> service.activate("John.Doe"));
    }

    @Test
    @DisplayName("Deactivate → should deactivate active trainee")
    void deactivate_shouldWork() {
        trainee.setActive(true);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.deactivate("John.Doe");

        assertFalse(trainee.getActive());
    }

    @Test
    @DisplayName("Deactivate → should throw if already inactive")
    void deactivate_shouldThrow() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class,
                () -> service.deactivate("John.Doe"));
    }

    @Test
    @DisplayName("UpdateTraineeTrainers → should replace trainers list")
    void updateTraineeTrainers_shouldReplaceList() {
        trainee.setTrainers(new HashSet<>(Set.of(new Trainer())));

        List<Long> ids = List.of(1L, 2L);
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(ids)).thenReturn(trainers);

        service.updateTraineeTrainers("John.Doe", ids);

        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    @DisplayName("GetUnassignedTrainers → should return list")
    void getUnassigned_shouldReturnList() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findNotAssignedToTrainee("John.Doe"))
                .thenReturn(List.of(new Trainer()));

        List<Trainer> result = service.getUnassignedTrainers("John.Doe");

        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("GetByUsername → should return trainee")
    void getByUsername_shouldReturn() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertEquals(trainee, service.getByUsername("John.Doe"));
    }

    @Test
    @DisplayName("GetByUsername → should throw when not found")
    void getByUsername_shouldThrow() {
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> service.getByUsername("John.Doe"));
    }

    @Test
    @DisplayName("GetAll → should return list")
    void getAll_shouldReturn() {
        when(traineeRepository.findAll()).thenReturn(List.of(trainee));

        assertEquals(1, service.getAll().size());
    }

    @Test
    @DisplayName("DeleteByUsername → should call repository")
    void delete_shouldCallRepo() {
        service.deleteByUsername("John.Doe");

        verify(traineeRepository).deleteByUsername("John.Doe");
    }
}