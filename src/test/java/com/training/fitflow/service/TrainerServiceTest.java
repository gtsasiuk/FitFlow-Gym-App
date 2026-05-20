package com.training.fitflow.service;

import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.exception.SpecializationNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.mapper.TrainerMapper;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingTypeRepository;
import com.training.fitflow.util.PasswordGenerator;
import com.training.fitflow.util.UsernameGenerator;
import com.training.fitflow.util.UserUpdateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerService Tests")
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerMapper trainerMapper;

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
        type = new TrainingType();
        type.setId(1L);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setUsername("John.Doe");
        trainer.setPassword("pass");
        trainer.setActive(false);
        trainer.setSpecialization(type);
    }

    @Test
    @DisplayName("Create → should create trainer successfully")
    void create_shouldCreateTrainer() {
        TrainerCreateRequest request = new TrainerCreateRequest(
                "John",
                "Doe",
                1L
        );

        Trainer mapped = new Trainer();
        mapped.setFirstName("John");
        mapped.setLastName("Doe");

        TrainerCreateResponse response =
                new TrainerCreateResponse("John.Doe", "pass123");

        when(trainingTypeRepository.findById(1L))
                .thenReturn(Optional.of(type));

        when(trainerMapper.toEntity(request))
                .thenReturn(mapped);

        when(usernameGenerator.generate("John", "Doe"))
                .thenReturn("John.Doe");

        when(passwordGenerator.generate())
                .thenReturn("pass123");

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(trainerMapper.toCreateResponse(any(Trainer.class)))
                .thenReturn(response);

        TrainerCreateResponse result = service.create(request);

        assertEquals("John.Doe", mapped.getUsername());
        assertEquals("pass123", mapped.getPassword());
        assertTrue(mapped.getActive());
        assertEquals(type, mapped.getSpecialization());

        assertEquals("John.Doe", result.username());
        assertEquals("pass123", result.password());

        verify(trainerRepository).save(mapped);
    }

    @Test
    @DisplayName("Create → should throw when specialization not found")
    void create_shouldThrow_whenSpecializationMissing() {
        TrainerCreateRequest request = new TrainerCreateRequest(
                "John",
                "Doe",
                99L
        );

        when(trainingTypeRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SpecializationNotFoundException.class,
                () -> service.create(request)
        );

        verify(trainerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update → should update trainer successfully")
    void update_shouldUpdateTrainer() {
        TrainerUpdateRequest request = new TrainerUpdateRequest(
                "Jane",
                "Smith",
                true
        );

        TrainerProfileResponse response =
                mock(TrainerProfileResponse.class);

        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(trainerMapper.toUpdateResponse(any(Trainer.class)))
                .thenReturn(mock(TrainerUpdateResponse.class));

        try (MockedStatic<UserUpdateUtil> mocked =
                     mockStatic(UserUpdateUtil.class)) {

            mocked.when(() ->
                    UserUpdateUtil.updateNameFields(
                            any(),
                            anyString(),
                            anyString(),
                            any()
                    )
            ).thenAnswer(inv -> null);

            TrainerUpdateResponse result =
                    service.update("John.Doe", request);

            assertNotNull(result);
            verify(trainerRepository).save(trainer);
        }
    }

    @Test
    @DisplayName("Update → should throw when trainer not found")
    void update_shouldThrow_whenNotFound() {
        TrainerUpdateRequest request = new TrainerUpdateRequest(
                "Jane",
                "Smith",
                true
        );

        when(trainerRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.update("missing", request)
        );
    }

    @Test
    @DisplayName("Activate → should activate trainer")
    void activate_shouldActivate() {
        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        service.activate("John.Doe");

        assertTrue(trainer.getActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("Activate → should throw if already active")
    void activate_shouldThrow_ifActive() {
        trainer.setActive(true);

        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        assertThrows(
                IllegalStateException.class,
                () -> service.activate("John.Doe")
        );
    }

    @Test
    @DisplayName("Activate → should throw when not found")
    void activate_shouldThrow_whenNotFound() {
        when(trainerRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.activate("missing")
        );
    }

    @Test
    @DisplayName("Deactivate → should deactivate trainer")
    void deactivate_shouldDeactivate() {
        trainer.setActive(true);

        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        service.deactivate("John.Doe");

        assertFalse(trainer.getActive());
        verify(trainerRepository).save(trainer);
    }

    @Test
    @DisplayName("Deactivate → should throw if already inactive")
    void deactivate_shouldThrow_ifInactive() {
        trainer.setActive(false);

        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        assertThrows(
                IllegalStateException.class,
                () -> service.deactivate("John.Doe")
        );
    }

    @Test
    @DisplayName("Deactivate → should throw when not found")
    void deactivate_shouldThrow_whenNotFound() {
        when(trainerRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.deactivate("missing")
        );
    }

    @Test
    @DisplayName("GetByUsername → should return profile")
    void getByUsername_shouldReturnProfile() {
        TrainerProfileResponse response =
                mock(TrainerProfileResponse.class);

        when(trainerRepository.findByUsername("John.Doe"))
                .thenReturn(Optional.of(trainer));

        when(trainerMapper.toProfileResponse(trainer))
                .thenReturn(response);

        TrainerProfileResponse result =
                service.getByUsername("John.Doe");

        assertNotNull(result);
        verify(trainerMapper).toProfileResponse(trainer);
    }

    @Test
    @DisplayName("GetByUsername → should throw when not found")
    void getByUsername_shouldThrow() {
        when(trainerRepository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.getByUsername("missing")
        );
    }

    @Test
    @DisplayName("GetAll → should return all trainers")
    void getAll_shouldReturnAll() {
        when(trainerRepository.findAll())
                .thenReturn(List.of(trainer));

        List<Trainer> result = service.getAll();

        assertEquals(1, result.size());
        verify(trainerRepository).findAll();
    }
}