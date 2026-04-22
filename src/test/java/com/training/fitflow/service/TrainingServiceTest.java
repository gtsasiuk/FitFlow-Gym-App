package com.training.fitflow.service;

import com.training.fitflow.dao.TraineeDao;
import com.training.fitflow.dao.TrainerDao;
import com.training.fitflow.dao.TrainingDao;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.exception.TrainingNotFoundException;
import com.training.fitflow.model.Training;
import com.training.fitflow.model.TrainingType;
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
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService service;

    private Training training;

    @BeforeEach
    void setUp() {
        training = new Training(
                1L,
                10L,
                20L,
                "Morning Workout",
                TrainingType.FITNESS,
                LocalDate.of(2024, 1, 1),
                60
        );
    }

    @Test
    @DisplayName("Create → should save training when trainee and trainer exist")
    void create_shouldSaveTraining_whenTraineeAndTrainerExist() {
        when(trainerDao.findTrainerById(20L)).thenReturn(Optional.of(mock()));
        when(traineeDao.findTraineeById(10L)).thenReturn(Optional.of(mock()));
        when(trainingDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Training result = service.create(training);

        assertEquals(training, result);
        verify(trainingDao).save(training);
    }

    @Test
    @DisplayName("Create → should throw exception when trainer not found")
    void create_shouldThrowException_whenTrainerNotFound() {
        when(trainerDao.findTrainerById(20L)).thenReturn(Optional.empty());

        TrainerNotFoundException ex = assertThrows(TrainerNotFoundException.class,
                () -> service.create(training));

        assertEquals("Trainer with id=20 not found", ex.getMessage());
        verify(trainingDao, never()).save(any());
    }

    @Test
    @DisplayName("Create → should throw exception when trainee not found")
    void create_shouldThrowException_whenTraineeNotFound() {
        when(trainerDao.findTrainerById(20L)).thenReturn(Optional.of(mock()));
        when(traineeDao.findTraineeById(10L)).thenReturn(Optional.empty());

        TraineeNotFoundException ex = assertThrows(TraineeNotFoundException.class,
                () -> service.create(training));

        assertEquals("Trainee with id=10 not found", ex.getMessage());
        verify(trainingDao, never()).save(any());
    }

    @Test
    @DisplayName("GetById → should return training when it exists")
    void getById_shouldReturnTraining_whenExists() {
        when(trainingDao.findTrainingById(1L)).thenReturn(Optional.of(training));

        Training result = service.getById(1L);

        assertEquals(training, result);
    }

    @Test
    @DisplayName("GetById → should throw exception when training not found")
    void getById_shouldThrowException_whenNotFound() {
        when(trainingDao.findTrainingById(1L)).thenReturn(Optional.empty());

        TrainingNotFoundException ex = assertThrows(TrainingNotFoundException.class,
                () -> service.getById(1L));

        assertEquals("Training with id=1 not found", ex.getMessage());
    }

    @Test
    @DisplayName("GetAll → should return all trainings")
    void getAll_shouldReturnAllTrainings() {
        when(trainingDao.findAllTrainings()).thenReturn(List.of(training));

        List<Training> result = service.getAll();

        assertEquals(1, result.size());
        verify(trainingDao).findAllTrainings();
    }

    @Test
    @DisplayName("Training.toString() → should contain main fields")
    void toString_shouldContainAllFields() {
        String result = training.toString();

        assertAll(
                () -> assertTrue(result.contains("1")),
                () -> assertTrue(result.contains("10")),
                () -> assertTrue(result.contains("20")),
                () -> assertTrue(result.contains("Morning Workout")),
                () -> assertTrue(result.contains("Fitness")),
                () -> assertTrue(result.contains("60"))
        );
    }
}