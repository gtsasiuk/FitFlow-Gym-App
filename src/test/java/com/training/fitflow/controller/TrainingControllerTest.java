package com.training.fitflow.controller;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
import com.training.fitflow.service.TrainingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingController Tests")
class TrainingControllerTest {
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @Test
    @DisplayName("createTraining → valid request → returns 201 CREATED")
    void createTraining_validRequest_returns201() {

        TrainingCreateRequest request =
                new TrainingCreateRequest(
                        "trainee1",
                        "trainer1",
                        "Strength",
                        LocalDate.of(2025, 1, 10),
                        60
                );

        ResponseEntity<Void> response =
                trainingController.createTraining(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNull(response.getBody());

        verify(trainingService).create(request);
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    @DisplayName("createTraining → service throws exception → propagates")
    void createTraining_serviceThrowsException_propagates() {

        TrainingCreateRequest request =
                new TrainingCreateRequest(
                        "trainee1",
                        "trainer1",
                        "Strength",
                        LocalDate.of(2025, 1, 10),
                        60
                );

        doThrow(new RuntimeException("Creation failed"))
                .when(trainingService).create(request);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> trainingController.createTraining(request)
        );

        assertEquals("Creation failed", ex.getMessage());

        verify(trainingService).create(request);
    }

    @Test
    @DisplayName("getTraineeTrainings → valid request → returns list")
    void getTraineeTrainings_valid_returnsList() {

        List<TraineeTrainingResponse> serviceResponse = List.of(
                new TraineeTrainingResponse(
                        "Strength",
                        LocalDate.of(2025, 1, 10),
                        "Fitness",
                        60,
                        "trainer1"
                )
        );

        when(trainingService.getTraineeTrainings(
                "trainee1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "trainer1",
                1L
        )).thenReturn(serviceResponse);

        ResponseEntity<List<TraineeTrainingResponse>> response =
                trainingController.getTraineeTrainings(
                        "trainee1",
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 31),
                        "trainer1",
                        1L
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(trainingService).getTraineeTrainings(
                "trainee1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "trainer1",
                1L
        );
    }

    @Test
    @DisplayName("getTraineeTrainings → minimal filters → returns list")
    void getTraineeTrainings_minimalFilters_returnsList() {

        List<TraineeTrainingResponse> serviceResponse = List.of();

        when(trainingService.getTraineeTrainings(
                "trainee1",
                null,
                null,
                null,
                null
        )).thenReturn(serviceResponse);

        ResponseEntity<List<TraineeTrainingResponse>> response =
                trainingController.getTraineeTrainings(
                        "trainee1",
                        null,
                        null,
                        null,
                        null
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());

        verify(trainingService).getTraineeTrainings(
                "trainee1",
                null,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("getTrainerTrainings → valid request → returns list")
    void getTrainerTrainings_valid_returnsList() {

        List<TrainerTrainingResponse> serviceResponse = List.of(
                new TrainerTrainingResponse(
                        "Strength",
                        LocalDate.of(2025, 1, 10),
                        "Fitness",
                        60,
                        "trainee1"
                )
        );

        when(trainingService.getTrainerTrainings(
                "trainer1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "trainee1"
        )).thenReturn(serviceResponse);

        ResponseEntity<List<TrainerTrainingResponse>> response =
                trainingController.getTrainerTrainings(
                        "trainer1",
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 12, 31),
                        "trainee1"
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        assertEquals(1, response.getBody().size());

        verify(trainingService).getTrainerTrainings(
                "trainer1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                "trainee1"
        );
    }

    @Test
    @DisplayName("getTrainerTrainings → minimal filters → returns list")
    void getTrainerTrainings_minimalFilters_returnsList() {

        List<TrainerTrainingResponse> serviceResponse = List.of();

        when(trainingService.getTrainerTrainings(
                "trainer1",
                null,
                null,
                null
        )).thenReturn(serviceResponse);

        ResponseEntity<List<TrainerTrainingResponse>> response =
                trainingController.getTrainerTrainings(
                        "trainer1",
                        null,
                        null,
                        null
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());

        verify(trainingService).getTrainerTrainings(
                "trainer1",
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("createTraining → service called exactly once")
    void createTraining_service_calledOnce() {

        TrainingCreateRequest request =
                new TrainingCreateRequest(
                        "trainee1",
                        "trainer1",
                        "Strength",
                        LocalDate.now(),
                        60
                );

        trainingController.createTraining(request);

        verify(trainingService, times(1)).create(request);
        verifyNoMoreInteractions(trainingService);
    }
}