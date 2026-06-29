package com.training.fitflow.controller;

import com.training.fitflow.client.WorkloadIntegrationService;
import com.training.fitflow.client.dto.TrainerWorkloadResponse;
import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.exception.InvalidWorkloadQueryException;
import com.training.fitflow.exception.TrainerNotFoundException;
import com.training.fitflow.service.TrainerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainerController Tests")
class TrainerControllerTest {
    @Mock
    private TrainerService trainerService;
    @Mock
    private WorkloadIntegrationService workloadIntegrationService;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    @DisplayName("createTrainer → valid request → returns 201 CREATED")
    void createTrainer_validRequest_returns201() {
        TrainerCreateRequest request = new TrainerCreateRequest("John", "Doe", 1L);
        TrainerCreateResponse serviceResponse = new TrainerCreateResponse("john.doe", "pass123");

        when(trainerService.create(request)).thenReturn(serviceResponse);

        ResponseEntity<TrainerCreateResponse> response = trainerController.createTrainer(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals(serviceResponse, response.getBody());

        verify(trainerService).create(request);
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @DisplayName("createTrainer → specialization not found → propagates exception")
    void createTrainer_specializationNotFound_propagatesException() {
        TrainerCreateRequest request = new TrainerCreateRequest("John", "Doe", 99L);

        when(trainerService.create(request)).thenThrow(new RuntimeException("Training type not found"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainerController.createTrainer(request));

        assertEquals("Training type not found", ex.getMessage());
        verify(trainerService).create(request);
    }

    @Test
    @DisplayName("getTrainerProfile → valid username → returns 200 OK")
    void getTrainerProfile_valid_returns200() {
        TrainerProfileResponse serviceResponse = mock(TrainerProfileResponse.class);

        when(trainerService.getByUsername("john.doe")).thenReturn(serviceResponse);
        ResponseEntity<TrainerProfileResponse> response = trainerController.getTrainerProfile("john.doe");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(serviceResponse, response.getBody());

        verify(trainerService).getByUsername("john.doe");
    }

    @Test
    @DisplayName("getTrainerProfile → trainer not found → throws exception")
    void getTrainerProfile_notFound_throwsException() {
        when(trainerService.getByUsername("missing.user")).thenThrow(new TrainerNotFoundException("missing.user"));

        TrainerNotFoundException ex = assertThrows(TrainerNotFoundException.class,
                () -> trainerController.getTrainerProfile("missing.user")
        );

        assertTrue(ex.getMessage().contains("missing.user"));
        verify(trainerService).getByUsername("missing.user");
    }

    @Test
    @DisplayName("updateStatus → active=true → calls activate")
    void updateStatus_active_callsActivate() {
        UserStatusUpdateRequest request = mock(UserStatusUpdateRequest.class);

        when(request.isActive()).thenReturn(true);

        ResponseEntity<Void> response = trainerController.updateStatus("john.doe", request);

        assertEquals(200, response.getStatusCode().value());

        verify(trainerService).activate("john.doe");
        verify(trainerService, never()).deactivate(any());
    }

    @Test
    @DisplayName("updateStatus → active=false → calls deactivate")
    void updateStatus_inactive_callsDeactivate() {
        UserStatusUpdateRequest request = mock(UserStatusUpdateRequest.class);

        when(request.isActive()).thenReturn(false);

        ResponseEntity<Void> response = trainerController.updateStatus("john.doe", request);

        assertEquals(200, response.getStatusCode().value());

        verify(trainerService).deactivate("john.doe");
        verify(trainerService, never()).activate(any());
    }

    @Test
    @DisplayName("updateStatus → activate already active → exception propagated")
    void updateStatus_activateAlreadyActive_exception() {
        UserStatusUpdateRequest request = mock(UserStatusUpdateRequest.class);

        when(request.isActive()).thenReturn(true);

        doThrow(new IllegalStateException("Trainer already active")).when(trainerService).activate("john.doe");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> trainerController.updateStatus("john.doe", request)
        );

        assertEquals("Trainer already active", ex.getMessage());

        verify(trainerService).activate("john.doe");
    }

    @Test
    @DisplayName("updateStatus → deactivate already inactive → exception propagated")
    void updateStatus_deactivateAlreadyInactive_exception() {
        UserStatusUpdateRequest request = mock(UserStatusUpdateRequest.class);
        when(request.isActive()).thenReturn(false);

        doThrow(new IllegalStateException("Trainer already inactive"))
                .when(trainerService).deactivate("john.doe");

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> trainerController.updateStatus("john.doe", request)
        );

        assertEquals("Trainer already inactive", ex.getMessage());

        verify(trainerService).deactivate("john.doe");
    }

    @Test
    @DisplayName("updateTrainerProfile → valid request → returns 200 OK")
    void updateTrainerProfile_valid_returns200() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("John", "Smith", true);
        TrainerUpdateResponse serviceResponse = mock(TrainerUpdateResponse.class);

        when(trainerService.update("john.doe", request)).thenReturn(serviceResponse);

        ResponseEntity<TrainerUpdateResponse> response = trainerController.updateTrainerProfile("john.doe", request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(serviceResponse, response.getBody());

        verify(trainerService).update("john.doe", request);
    }

    @Test
    @DisplayName("updateTrainerProfile → trainer not found → exception propagated")
    void updateTrainerProfile_notFound_exception() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("John", "Smith", true);

        when(trainerService.update("missing", request)).thenThrow(new TrainerNotFoundException("missing"));

        TrainerNotFoundException ex = assertThrows(TrainerNotFoundException.class,
                () -> trainerController.updateTrainerProfile("missing", request)
        );

        assertTrue(ex.getMessage().contains("missing"));

        verify(trainerService).update("missing", request);
    }

    @Test
    @DisplayName("getWorkload → no filters → returns workload")
    void getWorkload_noFilters_returnsWorkload() {
        TrainerWorkloadResponse serviceResponse = mock(TrainerWorkloadResponse.class);
        when(workloadIntegrationService.getWorkload("john.doe", null, null)).thenReturn(serviceResponse);

        TrainerWorkloadResponse response = trainerController.getWorkload("john.doe", null, null);

        assertEquals(serviceResponse, response);

        verify(workloadIntegrationService).getWorkload("john.doe", null, null);
    }

    @Test
    @DisplayName("getWorkload → year filter → returns workload")
    void getWorkload_yearFilter_returnsWorkload() {
        TrainerWorkloadResponse serviceResponse = mock(TrainerWorkloadResponse.class);

        when(workloadIntegrationService.getWorkload("john.doe", 2025, null))
                .thenReturn(serviceResponse);

        TrainerWorkloadResponse response = trainerController.getWorkload("john.doe", 2025, null);

        assertEquals(serviceResponse, response);

        verify(workloadIntegrationService)
                .getWorkload("john.doe", 2025, null);
    }

    @Test
    @DisplayName("getWorkload → year and month filters → returns workload")
    void getWorkload_yearAndMonth_returnsWorkload() {

        TrainerWorkloadResponse serviceResponse =
                mock(TrainerWorkloadResponse.class);

        when(workloadIntegrationService.getWorkload(
                "john.doe",
                2025,
                6))
                .thenReturn(serviceResponse);

        TrainerWorkloadResponse response =
                trainerController.getWorkload(
                        "john.doe",
                        2025,
                        6);

        assertEquals(serviceResponse, response);

        verify(workloadIntegrationService)
                .getWorkload("john.doe", 2025, 6);
    }

    @Test
    @DisplayName("getWorkload → month without year → throws exception")
    void getWorkload_monthWithoutYear_throwsException() {
        InvalidWorkloadQueryException ex =
                assertThrows(InvalidWorkloadQueryException.class,
                        () -> trainerController.getWorkload("john.doe", null, 6)
                );

        assertEquals("Year must be provided when month is specified", ex.getMessage());

        verifyNoInteractions(workloadIntegrationService);
    }

    @Test
    @DisplayName("getWorkload → integration service error → propagates exception")
    void getWorkload_serviceThrows_propagatesException() {
        when(workloadIntegrationService.getWorkload("john.doe", null, null))
                .thenThrow(new RuntimeException("Workload service unavailable"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                        () -> trainerController.getWorkload("john.doe", null, null)
                );

        assertEquals("Workload service unavailable", ex.getMessage());
        verify(workloadIntegrationService).getWorkload("john.doe", null, null);
    }

    @Test
    @DisplayName("service called exactly once for create")
    void create_serviceCalledOnce() {
        TrainerCreateRequest request = new TrainerCreateRequest("John", "Doe", 1L);

        when(trainerService.create(request)).thenReturn(mock(TrainerCreateResponse.class));

        trainerController.createTrainer(request);

        verify(trainerService, times(1)).create(request);
        verifyNoMoreInteractions(trainerService);
    }

    @Test
    @DisplayName("service called exactly once for update")
    void update_serviceCalledOnce() {
        TrainerUpdateRequest request = new TrainerUpdateRequest("John", "Doe", true);

        when(trainerService.update("john", request)).thenReturn(mock(TrainerUpdateResponse.class));

        trainerController.updateTrainerProfile("john", request);
        verify(trainerService, times(1)).update("john", request);
    }

}