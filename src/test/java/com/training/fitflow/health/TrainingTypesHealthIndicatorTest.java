package com.training.fitflow.health;

import com.training.fitflow.repository.TrainingTypeRepository;
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
@DisplayName("TrainingTypesHealthIndicator Tests")
class TrainingTypesHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypesHealthIndicator indicator;

    @Test
    @DisplayName("health → enough training types → UP")
    void health_enoughTrainingTypes_returnsUp() {
        when(trainingTypeRepository.count()).thenReturn(10L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(10L, health.getDetails().get("count"));
        verify(trainingTypeRepository).count();
    }

    @Test
    @DisplayName("health → exactly minimum training types → UP")
    void health_exactlyMinimumTrainingTypes_returnsUp() {
        when(trainingTypeRepository.count()).thenReturn(5L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("count"));
    }

    @Test
    @DisplayName("health → less than minimum training types → DOWN")
    void health_lessThanMinimum_returnsDown() {
        when(trainingTypeRepository.count()).thenReturn(3L);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(3L, health.getDetails().get("count"));
        assertEquals(5, health.getDetails().get("minimumExpected"));
        assertNotNull(health.getDetails().get("reason"));
    }

    @Test
    @DisplayName("health → no training types → DOWN")
    void health_noTrainingTypes_returnsDown() {
        when(trainingTypeRepository.count()).thenReturn(0L);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(0L, health.getDetails().get("count"));
    }

    @Test
    @DisplayName("health → repository throws exception → DOWN")
    void health_repositoryThrows_returnsDown() {
        when(trainingTypeRepository.count())
                .thenThrow(new RuntimeException("DB connection failed"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertNotNull(health.getDetails().get("error"));
    }
}