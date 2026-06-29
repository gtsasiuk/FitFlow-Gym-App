package com.training.fitflow.service;

import com.training.fitflow.client.WorkloadIntegrationService;
import com.training.fitflow.client.dto.TrainerWorkloadRequest;
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
import com.training.fitflow.model.Training;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

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
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private WorkloadIntegrationService workloadIntegrationService;

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
        trainee.setTrainings(new HashSet<>());
    }

    // ---------------- CREATE ----------------

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

        TraineeCreateResponse response = new TraineeCreateResponse("John.Doe", "generatedPass");

        when(traineeMapper.toEntity(request)).thenReturn(mappedTrainee);
        when(usernameGenerator.generate("John", "Doe")).thenReturn("John.Doe");
        when(passwordGenerator.generate()).thenReturn("generatedPass");
        when(passwordEncoder.encode("generatedPass")).thenReturn("hashedPassword");

        when(traineeRepository.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(traineeMapper.toTraineeCreateResponse(any(Trainee.class), eq("generatedPass")))
                .thenReturn(response);

        TraineeCreateResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("John.Doe", result.username());
        assertEquals("generatedPass", result.password());

        verify(passwordEncoder).encode("generatedPass");
        verify(traineeRepository).save(mappedTrainee);
    }

    // ---------------- UPDATE ----------------

    @Test
    @DisplayName("Update → should update trainee fields")
    void update_shouldUpdateFields() {
        TraineeUpdateRequest request = new TraineeUpdateRequest(
                "Jane",
                "Smith",
                LocalDate.of(1999, 5, 5),
                "New address",
                false
        );

        TraineeUpdateResponse response = mock(TraineeUpdateResponse.class);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));
        when(traineeMapper.toTraineeUpdateResponse(any(Trainee.class))).thenReturn(response);

        TraineeUpdateResponse result = service.update("John.Doe", request);

        assertNotNull(result);
        assertEquals("Jane", trainee.getFirstName());
        assertEquals("Smith", trainee.getLastName());
        assertEquals("New address", trainee.getAddress());
        assertFalse(trainee.getActive());

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

        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> service.update("missing", request)
        );

        verify(traineeRepository, never()).save(any());
    }

    // ---------------- ACTIVATE / DEACTIVATE ----------------

    @Test
    @DisplayName("Activate → should activate inactive trainee")
    void activate_shouldActivateInactiveTrainee() {
        trainee.setActive(false);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.activate("John.Doe");

        assertTrue(trainee.getActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Deactivate → should deactivate active trainee")
    void deactivate_shouldDeactivateActiveTrainee() {
        trainee.setActive(true);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.deactivate("John.Doe");

        assertFalse(trainee.getActive());
        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("Activate → should throw when trainee not found")
    void activate_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(TraineeNotFoundException.class, () -> service.activate("missing"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Activate → should throw when trainee already active")
    void activate_shouldThrowWhenAlreadyActive() {
        trainee.setActive(true);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> service.activate("John.Doe"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deactivate → should throw when trainee not found")
    void deactivate_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());
        assertThrows(TraineeNotFoundException.class, () -> service.deactivate("missing"));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deactivate → should throw when trainee already inactive")
    void deactivate_shouldThrowWhenAlreadyInactive() {
        trainee.setActive(false);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        assertThrows(IllegalStateException.class, () -> service.deactivate("John.Doe"));
        verify(traineeRepository, never()).save(any());
    }

    // ---------------- GET BY USERNAME ----------------

    @Test
    @DisplayName("GetByUsername → should return trainee profile")
    void getByUsername_shouldReturnProfile() {
        TraineeProfileResponse response = mock(TraineeProfileResponse.class);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toProfileResponse(trainee)).thenReturn(response);

        TraineeProfileResponse result = service.getByUsername("John.Doe");

        assertNotNull(result);
        verify(traineeMapper).toProfileResponse(trainee);
    }

    @Test
    @DisplayName("GetByUsername → should throw when trainee not found")
    void getByUsername_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.getByUsername("missing"));
    }

    // ---------------- UPDATE TRAINERS ----------------

    @Test
    @DisplayName("UpdateTraineeTrainers → should replace trainers list")
    void updateTraineeTrainers_shouldReplaceList() {
        Trainer t1 = new Trainer();
        t1.setUsername("trainer1");

        Trainer t2 = new Trainer();
        t2.setUsername("trainer2");

        trainee.setTrainers(new HashSet<>(Set.of(new Trainer())));

        TraineeTrainersUpdateRequest request = new TraineeTrainersUpdateRequest(List.of("trainer1", "trainer2"));

        List<TrainerSummaryResponse> response = List.of(
                mock(TrainerSummaryResponse.class),
                mock(TrainerSummaryResponse.class)
        );

        when(traineeRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainee));

        when(trainerRepository.findAllByUsernameIn(request.trainerUsernames())).thenReturn(List.of(t1, t2));

        when(trainerMapper.toSummaryResponseList(anySet())).thenReturn(response);

        List<TrainerSummaryResponse> result = service.updateTraineeTrainers("John.Doe", request);

        assertEquals(2, trainee.getTrainers().size());
        assertEquals(2, result.size());

        verify(traineeRepository).save(trainee);
    }

    @Test
    @DisplayName("UpdateTraineeTrainers → should throw when trainee not found")
    void updateTraineeTrainers_shouldThrowWhenTraineeNotFound() {
        TraineeTrainersUpdateRequest request = new TraineeTrainersUpdateRequest(List.of("trainer1"));
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.updateTraineeTrainers("missing", request));
        verify(trainerRepository, never()).findAllByUsernameIn(any());
    }

    // ---------------- GET UNASSIGNED TRAINERS ----------------

    @Test
    @DisplayName("GetUnassignedTrainers → should return trainers")
    void getUnassignedTrainers_shouldReturnList() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        TrainerSummaryResponse response = mock(TrainerSummaryResponse.class);

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findNotAssignedToTrainee("John.Doe")).thenReturn(List.of(trainer));
        when(trainerMapper.toSummaryResponse(trainer)).thenReturn(response);

        List<TrainerSummaryResponse> result = service.getUnassignedTrainers("John.Doe");

        assertEquals(1, result.size());
        verify(trainerRepository).findNotAssignedToTrainee("John.Doe");
    }

    @Test
    @DisplayName("GetUnassignedTrainers → should throw when trainee not found")
    void getUnassignedTrainers_shouldThrowWhenNotFound() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class, () -> service.getUnassignedTrainers("missing"));
        verify(trainerRepository, never()).findNotAssignedToTrainee(any());
    }

    // ---------------- DELETE BY USERNAME ----------------

    @Test
    @DisplayName("DeleteByUsername → should delete trainee and send workload updates")
    void deleteByUsername_shouldDeleteTrainee() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.one");
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.of(2024, 1, 1));
        training.setDuration(60);

        trainee.setTrainers(new HashSet<>(Set.of(trainer)));
        trainee.setTrainings(new HashSet<>(Set.of(training)));

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        service.deleteByUsername("John.Doe");

        assertTrue(trainee.getTrainers().isEmpty());

        verify(traineeRepository).save(trainee);
        verify(traineeRepository).delete(trainee);
        verify(workloadIntegrationService, times(1)).sendWorkloadUpdate(any(TrainerWorkloadRequest.class));
    }

    @Test
    @DisplayName("DeleteByUsername → should delete even if workload service fails")
    void deleteByUsername_shouldHandleWorkloadFailure() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer.one");
        trainer.setFirstName("Trainer");
        trainer.setLastName("One");
        trainer.setActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.of(2024, 1, 1));
        training.setDuration(60);

        trainee.setTrainers(new HashSet<>(Set.of(trainer)));
        trainee.setTrainings(new HashSet<>(Set.of(training)));

        when(traineeRepository.findByUsername("John.Doe")).thenReturn(Optional.of(trainee));

        doThrow(new RuntimeException("fail")).when(workloadIntegrationService).sendWorkloadUpdate(any());

        assertDoesNotThrow(() -> service.deleteByUsername("John.Doe"));
        verify(traineeRepository).delete(trainee);
    }

    // ---------------- GET ALL ----------------
    @Test
    @DisplayName("GetAll → should return all trainees")
    void getAll_shouldReturnAllTrainees() {
        when(traineeRepository.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = service.getAll();

        assertEquals(1, result.size());
        verify(traineeRepository).findAll();
    }
}