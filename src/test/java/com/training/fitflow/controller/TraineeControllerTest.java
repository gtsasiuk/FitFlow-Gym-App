package com.training.fitflow.controller;

import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.request.TraineeUpdateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.dto.trainer.request.TraineeTrainersUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.exception.TraineeNotFoundException;
import com.training.fitflow.service.TraineeService;
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
@DisplayName("TraineeController Tests")
class TraineeControllerTest {
    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @Test
    @DisplayName("createTrainee → valid request → returns 201 CREATED")
    void createTrainee_validRequest_returnsCreated() {

        TraineeCreateRequest request =
                new TraineeCreateRequest(
                        "John",
                        "Doe",
                        LocalDate.of(2000, 1, 1),
                        "Kyiv"
                );

        TraineeCreateResponse serviceResponse =
                new TraineeCreateResponse(
                        "john.doe",
                        "pass123"
                );

        when(traineeService.create(request))
                .thenReturn(serviceResponse);

        ResponseEntity<TraineeCreateResponse> response =
                traineeController.createTrainee(request);

        assertNotNull(response);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(serviceResponse, response.getBody());

        verify(traineeService)
                .create(request);
    }

    @Test
    @DisplayName("createTrainee → service throws exception → propagates")
    void createTrainee_serviceThrowsException_propagates() {

        TraineeCreateRequest request =
                new TraineeCreateRequest(
                        "John",
                        "Doe",
                        null,
                        null
                );

        doThrow(new RuntimeException("Unexpected error"))
                .when(traineeService)
                .create(request);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> traineeController.createTrainee(request)
        );

        assertEquals(
                "Unexpected error",
                exception.getMessage()
        );

        verify(traineeService)
                .create(request);
    }

    @Test
    @DisplayName("getTraineeProfile → existing trainee → returns profile")
    void getTraineeProfile_existingTrainee_returnsProfile() {

        TrainerSummaryResponse trainer =
                new TrainerSummaryResponse(
                        "trainer1",
                        "Mike",
                        "Tyson",
                        new SpecializationResponse(1L, "Fitness")
                );

        TraineeProfileResponse serviceResponse =
                new TraineeProfileResponse(
                        "John",
                        "Doe",
                        LocalDate.of(2000, 1, 1),
                        "Kyiv",
                        true,
                        List.of(trainer)
                );

        when(traineeService.getByUsername("john.doe"))
                .thenReturn(serviceResponse);

        ResponseEntity<TraineeProfileResponse> response =
                traineeController.getTraineeProfile("john.doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceResponse, response.getBody());

        verify(traineeService)
                .getByUsername("john.doe");
    }

    @Test
    @DisplayName("getTraineeProfile → trainee not found → throws exception")
    void getTraineeProfile_notFound_throwsException() {

        doThrow(new TraineeNotFoundException("john.doe"))
                .when(traineeService)
                .getByUsername("john.doe");

        assertThrows(
                TraineeNotFoundException.class,
                () -> traineeController.getTraineeProfile("john.doe")
        );

        verify(traineeService)
                .getByUsername("john.doe");
    }

    @Test
    @DisplayName("getUnassignedTrainers → returns trainers list")
    void getUnassignedTrainers_returnsList() {

        List<TrainerSummaryResponse> trainers = List.of(
                new TrainerSummaryResponse(
                        "trainer1",
                        "Mike",
                        "Tyson",
                        new SpecializationResponse(1L, "Fitness")
                ),
                new TrainerSummaryResponse(
                        "trainer2",
                        "John",
                        "Cena",
                        new SpecializationResponse(2L, "Cardio")
                )
        );

        when(traineeService.getUnassignedTrainers("john.doe"))
                .thenReturn(trainers);

        ResponseEntity<List<TrainerSummaryResponse>> response =
                traineeController.getUnassignedTrainers("john.doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(trainers, response.getBody());

        assertEquals(2, response.getBody().size());

        verify(traineeService)
                .getUnassignedTrainers("john.doe");
    }

    @Test
    @DisplayName("updateStatus → activate trainee → returns 200")
    void updateStatus_activateTrainee_returns200() {

        UserStatusUpdateRequest request =
                new UserStatusUpdateRequest(true);

        ResponseEntity<Void> response =
                traineeController.updateStatus("john.doe", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeService)
                .activate("john.doe");

        verify(traineeService, never())
                .deactivate(anyString());
    }

    @Test
    @DisplayName("updateStatus → deactivate trainee → returns 200")
    void updateStatus_deactivateTrainee_returns200() {

        UserStatusUpdateRequest request =
                new UserStatusUpdateRequest(false);

        ResponseEntity<Void> response =
                traineeController.updateStatus("john.doe", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(traineeService)
                .deactivate("john.doe");

        verify(traineeService, never())
                .activate(anyString());
    }

    @Test
    @DisplayName("updateTraineeProfile → valid request → returns updated profile")
    void updateTraineeProfile_validRequest_returnsUpdatedProfile() {

        TraineeUpdateRequest request =
                new TraineeUpdateRequest(
                        "John",
                        "Doe",
                        LocalDate.of(2000, 1, 1),
                        "Lviv",
                        true
                );

        TraineeUpdateResponse serviceResponse =
                new TraineeUpdateResponse(
                        "john.doe",
                        "John",
                        "Doe",
                        LocalDate.of(2000, 1, 1),
                        "Lviv",
                        true,
                        List.of()
                );

        when(traineeService.update("john.doe", request))
                .thenReturn(serviceResponse);

        ResponseEntity<TraineeUpdateResponse> response =
                traineeController.updateTraineeProfile("john.doe", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceResponse, response.getBody());

        verify(traineeService)
                .update("john.doe", request);
    }

    @Test
    @DisplayName("updateTraineeProfile trainers → returns updated trainers list")
    void updateTraineeProfileTrainers_returnsUpdatedList() {

        TraineeTrainersUpdateRequest request =
                new TraineeTrainersUpdateRequest(
                        List.of("trainer1", "trainer2")
                );

        List<TrainerSummaryResponse> serviceResponse = List.of(
                new TrainerSummaryResponse(
                        "trainer1",
                        "Mike",
                        "Tyson",
                        new SpecializationResponse(1L, "Fitness")
                )
        );

        when(traineeService.updateTraineeTrainers("john.doe", request))
                .thenReturn(serviceResponse);

        ResponseEntity<List<TrainerSummaryResponse>> response =
                traineeController.updateTraineeProfile(
                        "john.doe",
                        request
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(serviceResponse, response.getBody());

        verify(traineeService)
                .updateTraineeTrainers("john.doe", request);
    }

    @Test
    @DisplayName("deleteTrainee → existing trainee → returns 204")
    void deleteTrainee_existingTrainee_returns204() {

        ResponseEntity<Void> response =
                traineeController.deleteTrainee("john.doe");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertNull(response.getBody());

        verify(traineeService)
                .deleteByUsername("john.doe");
    }

    @Test
    @DisplayName("deleteTrainee → trainee not found → throws exception")
    void deleteTrainee_notFound_throwsException() {

        doThrow(new TraineeNotFoundException("john.doe"))
                .when(traineeService)
                .deleteByUsername("john.doe");

        assertThrows(
                TraineeNotFoundException.class,
                () -> traineeController.deleteTrainee("john.doe")
        );

        verify(traineeService)
                .deleteByUsername("john.doe");
    }

    @Test
    @DisplayName("createTrainee → service called exactly once")
    void createTrainee_serviceCalledExactlyOnce() {

        TraineeCreateRequest request =
                new TraineeCreateRequest(
                        "John",
                        "Doe",
                        null,
                        null
                );

        traineeController.createTrainee(request);

        verify(traineeService, times(1))
                .create(request);

        verifyNoMoreInteractions(traineeService);
    }
}