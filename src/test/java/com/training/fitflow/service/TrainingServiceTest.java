package com.training.fitflow.service;

import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
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
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private TrainingService service;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(10L);
        trainee.setUsername("john.doe");

        trainer = new Trainer();
        trainer.setId(20L);
        trainer.setUsername("trainer.one");

        TrainingType type = new TrainingType(1L, "Fitness");

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName("Morning Workout");
        training.setType(type);
        training.setDate(LocalDate.of(2024, 1, 1));
        training.setDuration(60);
    }

    @Test
    @DisplayName("Create → should save training when valid")
    void create_shouldSaveTraining_whenValid() {
        when(trainerRepository.findById(20L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(10L)).thenReturn(Optional.of(trainee));
        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Training result = service.create(training);

        assertEquals("Morning Workout", result.getName());
        verify(trainingRepository).save(training);
    }

    @Test
    @DisplayName("Create → should throw when trainer not found")
    void create_shouldThrow_whenTrainerNotFound() {
        when(trainerRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> service.create(training));

        verify(trainingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create → should throw when trainee not found")
    void create_shouldThrow_whenTraineeNotFound() {
        when(trainerRepository.findById(20L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> service.create(training));

        verify(trainingRepository, never()).save(any());
    }

    // ---------------- GET BY ID ----------------

    @Test
    @DisplayName("GetById → should return training")
    void getById_shouldReturnTraining() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training result = service.getById(1L);

        assertEquals(training, result);
    }

    @Test
    @DisplayName("GetById → should throw when not found")
    void getById_shouldThrow() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TrainingNotFoundException.class,
                () -> service.getById(1L));
    }

    @Test
    @DisplayName("GetAll → should return list")
    void getAll_shouldReturnList() {
        when(trainingRepository.findAll()).thenReturn(List.of(training));

        List<Training> result = service.getAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTraineeTrainings → should return list when trainee exists")
    void getTraineeTrainings_shouldReturnList() {
        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.of(trainee));

        when(trainingRepository.findTraineeTrainings(
                any(), any(), any(), any(), any()
        )).thenReturn(List.of(training));

        List<Training> result = service.getTraineeTrainings(
                "john.doe", null, null, null, null
        );

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTraineeTrainings → should throw when trainee not found")
    void getTraineeTrainings_shouldThrow() {
        when(traineeRepository.findByUsername("john.doe"))
                .thenReturn(Optional.empty());

        assertThrows(TraineeNotFoundException.class,
                () -> service.getTraineeTrainings(
                        "john.doe", null, null, null, null
                ));
    }

    @Test
    @DisplayName("getTrainerTrainings → should return list when trainer exists")
    void getTrainerTrainings_shouldReturnList() {
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.of(trainer));

        when(trainingRepository.findTrainerTrainings(
                any(), any(), any(), any()
        )).thenReturn(List.of(training));

        List<Training> result = service.getTrainerTrainings(
                "trainer.one", null, null, null
        );

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getTrainerTrainings → should throw when trainer not found")
    void getTrainerTrainings_shouldThrow() {
        when(trainerRepository.findByUsername("trainer.one"))
                .thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> service.getTrainerTrainings(
                        "trainer.one", null, null, null
                ));
    }

    @Test
    @DisplayName("toString → should contain key fields")
    void toString_shouldContainFields() {
        String result = training.toString();

        assertAll(
                () -> assertTrue(result.contains("Morning Workout")),
                () -> assertTrue(result.contains("60")),
                () -> assertTrue(result.contains("1"))
        );
    }
}