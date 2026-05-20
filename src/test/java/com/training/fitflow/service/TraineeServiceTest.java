package com.training.fitflow.service;

import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.request.TraineeUpdateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.dto.trainer.request.TraineeTrainersUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.mapper.TraineeMapper;
import com.training.fitflow.mapper.TrainerMapper;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraineeService Tests")
class TraineeServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TraineeService service;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();

        trainee.setId(1L);
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setUsername("John.Doe");
        trainee.setPassword("password");
        trainee.setActive(true);
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("Test address");
        trainee.setTrainers(new HashSet<>());
    }

    @Test
    @DisplayName("Create → should generate credentials and save trainee")
    void create_shouldGenerateCredentialsAndSave() {
        TraineeCreateRequest request = new TraineeCreateRequest(
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "Address"
        );

        Trainee mappedTrainee = new Trainee();
        mappedTrainee.setFirstName("John");
        mappedTrainee.setLastName("Doe");

        TraineeCreateResponse response =
                new TraineeCreateResponse("John.Doe", "generatedPass");

        when(traineeMapper.toEntity(request)).thenReturn(mappedTrainee);
        when(usernameGenerator.generate("John", "Doe"))
                .thenReturn("John.Doe");
        when(passwordGenerator.generate())
                .thenReturn("generatedPass");
        when(traineeRepository.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(traineeMapper.toTraineeCreateResponse(any(Trainee.class)))
                .thenReturn(response);

        TraineeCreateResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("John.Doe", result.username());
        assertEquals("generatedPass", result.password());

        assertEquals("John.Doe", mappedTrainee.getUsername());
        assertEquals("generatedPass", mappedTrainee.getPassword());
        assertTrue(mappedTrainee.getActive());

        verify(traineeRepository).save(mappedTrainee);
    }

    @Test
    @DisplayName("Update → should update trainee fields")
    void update_shouldUpdateFields() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "Jane",
                "Smith",
                LocalDate.of(1999, 5, 5),
                "New address",
                true
        );

        TraineeUpdateResponse response = new TraineeUpdateResponse(
                "Jane.Smith",
                "Jane",
                "Smith",
                LocalDate.of(1999, 5, 5),
                "New address",
                true,
                List.of()
        );

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        when(usernameGenerator.generate("Jane", "Smith"))
                .thenReturn("Jane.Smith");

        when(traineeRepository.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(traineeMapper.toTraineeUpdateResponse(any(Trainee.class)))
                .thenReturn(response);

        TraineeUpdateResponse result =
                service.update("John.Doe", request);

        assertNotNull(result);
        assertEquals("Jane", trainee.getFirstName());
        assertEquals("Smith", trainee.getLastName());
        assertEquals("New address", trainee.getAddress());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Update → should throw when trainee not found")
    void update_shouldThrowWhenNotFound() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "Jane",
                "Smith",
                null,
                null,
                true
        );

        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.update("missing", request)
        );

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Activate → should activate inactive trainee")
    void activate_shouldActivateInactiveTrainee() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        service.activate("John.Doe");

        assertTrue(trainee.getActive());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Activate → should throw if trainee already active")
    void activate_shouldThrowIfAlreadyActive() {
        trainee.setActive(true);

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        assertThrows(
                IllegalStateException.class,
                () -> service.activate("John.Doe")
        );

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Activate → should throw when trainee not found")
    void activate_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.activate("missing")
        );
    }

    @Test
    @DisplayName("Deactivate → should deactivate active trainee")
    void deactivate_shouldDeactivateActiveTrainee() {
        trainee.setActive(true);

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        service.deactivate("John.Doe");

        assertFalse(trainee.getActive());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Deactivate → should throw if trainee already inactive")
    void deactivate_shouldThrowIfAlreadyInactive() {
        trainee.setActive(false);

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        assertThrows(
                IllegalStateException.class,
                () -> service.deactivate("John.Doe")
        );

        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deactivate → should throw when trainee not found")
    void deactivate_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.deactivate("missing")
        );
    }

    @Test
    @DisplayName("GetByUsername → should return trainee profile")
    void getByUsername_shouldReturnProfile() {
        TraineeProfileResponse response = new TraineeProfileResponse(
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "Address",
                true,
                List.of()
        );

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        when(traineeMapper.toProfileResponse(trainee))
                .thenReturn(response);

        TraineeProfileResponse result =
                service.getByUsername("John.Doe");

        assertNotNull(result);
        assertEquals("John", result.firstName());

        verify(traineeMapper).toProfileResponse(trainee);
    }

    @Test
    @DisplayName("GetByUsername → should throw when trainee not found")
    void getByUsername_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.getByUsername("missing")
        );
    }

    @Test
    @DisplayName("UpdateTraineeTrainers → should replace trainers list")
    void updateTraineeTrainers_shouldReplaceList() {
        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");

        trainee.setTrainers(new HashSet<>(Set.of(new Trainer())));

        TraineeTrainersUpdateRequest request =
                new TraineeTrainersUpdateRequest(
                        List.of("trainer1", "trainer2")
                );

        List<TrainerSummaryResponse> response = List.of(
                mock(TrainerSummaryResponse.class),
                mock(TrainerSummaryResponse.class)
        );

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllByUsernameIn(
                request.trainerUsernames()))
                .thenReturn(List.of(trainer1, trainer2));

        when(trainerMapper.toSummaryResponseList(anySet()))
                .thenReturn(response);

        List<TrainerSummaryResponse> result =
                service.updateTraineeTrainers("John.Doe", request);

        assertEquals(2, trainee.getTrainers().size());
        assertEquals(2, result.size());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("UpdateTraineeTrainers → should throw when trainee not found")
    void updateTraineeTrainers_shouldThrowWhenNotFound() {
        TraineeTrainersUpdateRequest request =
                new TraineeTrainersUpdateRequest(List.of("trainer1"));

        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.updateTraineeTrainers("missing", request)
        );
    }

    @Test
    @DisplayName("GetUnassignedTrainers → should return trainers list")
    void getUnassignedTrainers_shouldReturnList() {
        Trainer trainer = new Trainer();

        TrainerSummaryResponse response =
                mock(TrainerSummaryResponse.class);

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findNotAssignedToTrainee("John.Doe"))
                .thenReturn(List.of(trainer));

        when(trainerMapper.toSummaryResponse(trainer))
                .thenReturn(response);

        List<TrainerSummaryResponse> result =
                service.getUnassignedTrainers("John.Doe");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("GetUnassignedTrainers → should throw when trainee not found")
    void getUnassignedTrainers_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.getUnassignedTrainers("missing")
        );
    }

    @Test
    @DisplayName("GetAll → should return all trainees")
    void getAll_shouldReturnAllTrainees() {
        when(traineeRepository.findAll())
                .thenReturn(List.of(trainee));

        List<Trainee> result = service.getAll();

        assertEquals(1, result.size());

        verify(traineeRepository).findAll();
    }

    @Test
    @DisplayName("DeleteByUsername → should clear trainers and delete trainee")
    void deleteByUsername_shouldDeleteTrainee() {
        Trainer trainer = new Trainer();

        trainee.setTrainers(new HashSet<>(Set.of(trainer)));

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        service.deleteByUsername("John.Doe");

        assertTrue(trainee.getTrainers().isEmpty());

        verify(traineeRepository).save(trainee);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    @DisplayName("DeleteByUsername → should throw when trainee not found")
    void deleteByUsername_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.deleteByUsername("missing")
        );

        verify(traineeRepository, never()).delete(any());
    }
}