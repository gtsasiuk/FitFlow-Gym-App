package com.training.fitflow.controller;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.service.TrainingTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeController Tests")
class TrainingTypeControllerTest {
    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @Test
    @DisplayName("getAll → returns list of training types")
    void getAll_returnsList() {

        List<SpecializationResponse> serviceResponse = List.of(
                new SpecializationResponse(1L, "Fitness"),
                new SpecializationResponse(2L, "Cardio")
        );

        when(trainingTypeService.getAll())
                .thenReturn(serviceResponse);

        ResponseEntity<List<SpecializationResponse>> response =
                trainingTypeController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(trainingTypeService).getAll();
        verifyNoMoreInteractions(trainingTypeService);
    }

    @Test
    @DisplayName("getAll → empty list → returns empty response")
    void getAll_returnsEmptyList() {

        List<SpecializationResponse> serviceResponse = List.of();

        when(trainingTypeService.getAll())
                .thenReturn(serviceResponse);

        ResponseEntity<List<SpecializationResponse>> response =
                trainingTypeController.getAll();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceResponse, response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(trainingTypeService).getAll();
    }

    @Test
    @DisplayName("getAll → service called exactly once")
    void getAll_service_calledOnce() {

        trainingTypeController.getAll();

        verify(trainingTypeService, times(1)).getAll();
        verifyNoMoreInteractions(trainingTypeService);
    }
}