package com.training.fitflow.service;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.mapper.TrainingMapper;
import com.training.fitflow.model.Trainee;
import com.training.fitflow.model.Trainer;
import com.training.fitflow.model.Training;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingService Tests")
class TrainingServiceTest {
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingService service;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingType type;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(10L);
        trainee.setUsername("john.doe");

        trainer = new Trainer();
        trainer.setId(20L);
        trainer.setUsername("trainer.one");

        type = new TrainingType();
        type.setId(1L);
        type.setName("Fitness");

        trainer.setSpecialization(type);

        training = new Training();
        training.setId(1L);
        training.setName("Morning Workout");
        training.setDate(LocalDate.of(2024, 1, 1));
        training.setDuration(60);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setType(type);
    }

    // ---------------- CREATE ----------------

    @Test
    @DisplayName("Create → should save training when valid")
    void create_shouldSaveTraining_whenValid() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.one",
                "Morning Workout",
                LocalDate.of(2024, 1, 1),
                60
        );

        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.of(trainer));
        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainee));
        when(trainingRepository.save(any(Training.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        service.create(request);

        verify(trainingRepository).save(training);
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainee, training.getTrainee());
        assertEquals(type, training.getType());
    }

    @Test
    @DisplayName("Create → should throw when trainer not found")
    void create_shouldThrow_whenTrainerNotFound() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.one",
                "Workout",
                LocalDate.now(),
                60
        );

        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.create(request)
        );

        verify(trainingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create → should throw when trainee not found")
    void create_shouldThrow_whenTraineeNotFound() {
        TrainingCreateRequest request = new TrainingCreateRequest(
                "john.doe",
                "trainer.one",
                "Workout",
                LocalDate.now(),
                60
        );

        when(trainingMapper.toEntity(request)).thenReturn(training);
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.of(trainer));
        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.create(request)
        );

        verify(trainingRepository, never()).save(any());
    }

    // ---------------- GET BY ID ----------------

    @Test
    @DisplayName("GetById → should return training")
    void getById_shouldReturnTraining() {
        when(trainingRepository.findById(1L))
                .thenReturn(Optional.of(training));

        Training result = service.getById(1L);

        assertEquals(training, result);
    }

    @Test
    @DisplayName("GetById → should throw when not found")
    void getById_shouldThrow() {
        when(trainingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainingNotFoundException.class,
                () -> service.getById(1L)
        );
    }

    // ---------------- GET ALL ----------------

    @Test
    @DisplayName("GetAll → should return list")
    void getAll_shouldReturnList() {
        when(trainingRepository.findAll())
                .thenReturn(List.of(training));

        List<Training> result = service.getAll();

        assertEquals(1, result.size());
        verify(trainingRepository).findAll();
    }

    // ---------------- TRAINEE TRAININGS ----------------

    @Test
    @DisplayName("getTraineeTrainings → should return list")
    void getTraineeTrainings_shouldReturnList() {
        TraineeTrainingResponse response = mock(TraineeTrainingResponse.class);

        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainee));

        when(trainingRepository.findTraineeTrainings(
                eq("john.doe"),
                any(),
                any(),
                isNull(),
                isNull()
        )).thenReturn(List.of(training));

        when(trainingMapper.toTraineeTrainingResponse(training))
                .thenReturn(response);

        List<TraineeTrainingResponse> result =
                service.getTraineeTrainings("john.doe", null, null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTraineeTrainings → should throw when trainee not found")
    void getTraineeTrainings_shouldThrow() {
        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.empty());

        assertThrows(
                TraineeNotFoundException.class,
                () -> service.getTraineeTrainings("john.doe", null, null, null, null)
        );
    }

    // ---------------- TRAINER TRAININGS ----------------

    @Test
    @DisplayName("getTrainerTrainings → should return list")
    void getTrainerTrainings_shouldReturnList() {
        TrainerTrainingResponse response = mock(TrainerTrainingResponse.class);

        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.of(trainer));

        when(trainingRepository.findTrainerTrainings(
                eq("trainer.one"),
                any(),
                any(),
                isNull()
        )).thenReturn(List.of(training));

        when(trainingMapper.toTrainerTrainingResponse(training))
                .thenReturn(response);

        List<TrainerTrainingResponse> result =
                service.getTrainerTrainings("trainer.one", null, null, null);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTrainerTrainings → should throw when trainer not found")
    void getTrainerTrainings_shouldThrow() {
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.empty());

        assertThrows(
                TrainerNotFoundException.class,
                () -> service.getTrainerTrainings("trainer.one", null, null, null)
        );
    }
}