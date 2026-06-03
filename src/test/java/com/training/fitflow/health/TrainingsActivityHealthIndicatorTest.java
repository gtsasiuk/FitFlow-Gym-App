package com.training.fitflow.health;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
import com.training.fitflow.repository.TrainingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingsActivityHealthIndicator Tests")
class TrainingsActivityHealthIndicatorTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainingsActivityHealthIndicator indicator;

    @Test
    @DisplayName("health → trainings, trainers, trainees exist → UP")
    void health_allEntitiesExist_returnsUp() {
        when(trainingRepository.count()).thenReturn(50L);
        when(trainerRepository.count()).thenReturn(10L);
        when(traineeRepository.count()).thenReturn(20L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(50L, health.getDetails().get("trainings"));
        assertEquals(10L, health.getDetails().get("trainers"));
        assertEquals(20L, health.getDetails().get("trainees"));
    }

    @Test
    @DisplayName("health → empty system → UP")
    void health_emptySystem_returnsUp() {
        when(trainingRepository.count()).thenReturn(0L);
        when(trainerRepository.count()).thenReturn(0L);
        when(traineeRepository.count()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(0L, health.getDetails().get("trainings"));
    }

    @Test
    @DisplayName("health → users exist but no trainings → WARNING")
    void health_usersExistButNoTrainings_returnsWarning() {
        when(trainingRepository.count()).thenReturn(0L);
        when(trainerRepository.count()).thenReturn(5L);
        when(traineeRepository.count()).thenReturn(10L);

        Health health = indicator.health();

        assertEquals("WARNING", health.getStatus().getCode());
        assertEquals(0, health.getDetails().get("trainings"));
        assertEquals(5L, health.getDetails().get("trainers"));
        assertEquals(10L, health.getDetails().get("trainees"));
        assertNotNull(health.getDetails().get("reason"));
    }

    @Test
    @DisplayName("health → only trainers exist → UP")
    void health_onlyTrainersExist_returnsUp() {
        when(trainingRepository.count()).thenReturn(0L);
        when(trainerRepository.count()).thenReturn(5L);
        when(traineeRepository.count()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    @DisplayName("health → only trainees exist → UP")
    void health_onlyTraineesExist_returnsUp() {
        when(trainingRepository.count()).thenReturn(0L);
        when(trainerRepository.count()).thenReturn(0L);
        when(traineeRepository.count()).thenReturn(10L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    @DisplayName("health → training repository throws → DOWN")
    void health_trainingRepositoryThrows_returnsDown() {
        when(trainingRepository.count())
                .thenThrow(new RuntimeException("DB connection refused"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertNotNull(health.getDetails().get("error"));
    }
}