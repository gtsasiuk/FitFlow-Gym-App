package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {
    @Mock
    private TraineeRepository repository;
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
                null,
                LocalDate.of(2000, 1, 1),
                "Address"
        );
    }

    @Test
    @DisplayName("Create → should generate username and password, then save trainee")
    void create_shouldGenerateUsernamePasswordAndSaveTrainee() {
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("password12");
        when(repository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = service.create(trainee);

        assertEquals("John.Doe", result.getUsername());
        assertEquals("password12", result.getPassword());
        assertTrue(result.getActive());

        verify(repository).save(trainee);
    }

    @Test
    @DisplayName("Update → should keep username unchanged when name remains the same")
    void update_shouldUpdateFieldsWithoutChangingUsernameIfNameSame() {
        Trainee existing = new Trainee(
                1L, "John", "Doe", "John.Doe", "pass", true,
                LocalDate.of(2000, 1, 1), "Old address"
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setAddress("New address");

        Trainee result = service.update(trainee);

        assertEquals("John.Doe", result.getUsername());
        assertEquals("New address", result.getAddress());
    }

    @Test
    @DisplayName("Update → should regenerate username when name changes")
    void update_shouldRegenerateUsernameIfNameChanged() {
        Trainee existing = new Trainee(
                1L, "John", "Doe", "John.Doe", "pass", true,
                LocalDate.of(2000, 1, 1), "Old address"
        );

        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(existing));
        when(usernameGenerator.generate("Jane", "Smith")).thenReturn("Jane.Smith");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        trainee.setFirstName("Jane");
        trainee.setLastName("Smith");

        Trainee result = service.update(trainee);

        assertEquals("Jane.Smith", result.getUsername());
    }

    @Test
    @DisplayName("GetByUsername → should return trainee when it exists")
    void getById_shouldReturnTrainee_whenExists() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        Trainee result = service.getByUsername("John.Doe");

        assertEquals(trainee, result);
    }

    @Test
    @DisplayName("GetByUsername → should throw exception when trainee not found")
    void getById_shouldThrowException_whenNotFound() {
        when(repository.findByUsername("John.Doe")).thenReturn(Optional.empty());

        TraineeNotFoundException ex = assertThrows(TraineeNotFoundException.class,
                () -> service.getByUsername("John.Doe"));

        assertEquals("Trainee with username=John.Doe not found", ex.getMessage());
    }

    @Test
    @DisplayName("GetAll → should return all trainees from repository")
    void getAll_shouldReturnAllTrainees() {
        when(repository.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = service.getAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("DeleteByUsername → should call DAO delete method with correct ID")
    void deleteById_shouldCallDaoDelete() {
        service.deleteByUsername("John.Doe");

        verify(repository).deleteByUsername("John.Doe");
    }

    @Test
    @DisplayName("Trainee.toString() → should contain all main fields")
    void toString_shouldContainAllFields() {
        Trainee trainee = new Trainee(
                1L,
                "John",
                "Doe",
                "John.Doe",
                "password12",
                true,
                LocalDate.of(2000, 1, 1),
                "Address"
        );

        String result = trainee.toString();

        assertAll(
                () -> assertTrue(result.contains("John")),
                () -> assertTrue(result.contains("Doe")),
                () -> assertTrue(result.contains("John.Doe")),
                () -> assertTrue(result.contains("password12")),
                () -> assertTrue(result.contains("Address"))
        );
    }
}