package com.training.fitflow.health;

import com.training.fitflow.repository.TraineeRepository;
import com.training.fitflow.repository.TrainerRepository;
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
@DisplayName("ActiveUsersHealthIndicator Tests")
class ActiveUsersHealthIndicatorTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private ActiveUsersHealthIndicator indicator;

    @Test
    @DisplayName("health → active trainers and trainees exist → UP")
    void health_activeUsersExist_returnsUp() {
        when(trainerRepository.count()).thenReturn(10L);
        when(traineeRepository.count()).thenReturn(20L);
        when(trainerRepository.countByActiveTrue()).thenReturn(7L);
        when(traineeRepository.countByActiveTrue()).thenReturn(15L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(10L, health.getDetails().get("totalTrainers"));
        assertEquals(7L, health.getDetails().get("activeTrainers"));
        assertEquals(20L, health.getDetails().get("totalTrainees"));
        assertEquals(15L, health.getDetails().get("activeTrainees"));
    }

    @Test
    @DisplayName("health → no users at all → UP")
    void health_noUsersAtAll_returnsUp() {
        when(trainerRepository.count()).thenReturn(0L);
        when(traineeRepository.count()).thenReturn(0L);
        when(trainerRepository.countByActiveTrue()).thenReturn(0L);
        when(traineeRepository.countByActiveTrue()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(0L, health.getDetails().get("totalTrainers"));
        assertEquals(0L, health.getDetails().get("totalTrainees"));
    }

    @Test
    @DisplayName("health → trainers exist but all deactivated → DEGRADED")
    void health_allTrainersDeactivated_returnsDegraded() {
        when(trainerRepository.count()).thenReturn(5L);
        when(traineeRepository.count()).thenReturn(10L);
        when(trainerRepository.countByActiveTrue()).thenReturn(0L);
        when(traineeRepository.countByActiveTrue()).thenReturn(8L);

        Health health = indicator.health();

        assertEquals("DEGRADED", health.getStatus().getCode());
        assertEquals(5L, health.getDetails().get("totalTrainers"));
        assertEquals(0, health.getDetails().get("activeTrainers"));
        assertNotNull(health.getDetails().get("reason"));
    }

    @Test
    @DisplayName("health → only trainees deactivated → UP")
    void health_onlyTraineesDeactivated_returnsUp() {
        when(trainerRepository.count()).thenReturn(5L);
        when(traineeRepository.count()).thenReturn(10L);
        when(trainerRepository.countByActiveTrue()).thenReturn(3L);
        when(traineeRepository.countByActiveTrue()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    @DisplayName("health → trainer repository throws → DOWN")
    void health_trainerRepositoryThrows_returnsDown() {
        when(trainerRepository.count())
                .thenThrow(new RuntimeException("DB connection failed"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertNotNull(health.getDetails().get("error"));
    }

    @Test
    @DisplayName("health → trainee repository throws → DOWN")
    void health_traineeRepositoryThrows_returnsDown() {
        when(trainerRepository.count()).thenReturn(5L);
        when(traineeRepository.count())
                .thenThrow(new RuntimeException("DB error"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}