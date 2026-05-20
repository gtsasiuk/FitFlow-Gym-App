package com.training.fitflow.service;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.mapper.TrainingTypeMapper;
import com.training.fitflow.model.TrainingType;
import com.training.fitflow.repository.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrainingTypeService Tests")
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingTypeService service;

    @Test
    @DisplayName("getAll → should return mapped specialization list")
    void getAll_shouldReturnMappedList() {

        TrainingType type1 = new TrainingType(1L, "Fitness");
        TrainingType type2 = new TrainingType(2L, "Yoga");

        SpecializationResponse response1 =
                new SpecializationResponse(1L, "Fitness");

        SpecializationResponse response2 =
                new SpecializationResponse(2L, "Yoga");

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(type1, type2));

        when(trainingTypeMapper.toResponse(type1))
                .thenReturn(response1);

        when(trainingTypeMapper.toResponse(type2))
                .thenReturn(response2);

        List<SpecializationResponse> result = service.getAll();

        assertEquals(2, result.size());

        assertEquals("Fitness", result.get(0).name());
        assertEquals("Yoga", result.get(1).name());

        verify(trainingTypeRepository).findAll();
        verify(trainingTypeMapper).toResponse(type1);
        verify(trainingTypeMapper).toResponse(type2);
    }

    @Test
    @DisplayName("getAll → should return empty list when no data")
    void getAll_shouldReturnEmptyList() {

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of());

        List<SpecializationResponse> result = service.getAll();

        assertTrue(result.isEmpty());

        verify(trainingTypeRepository).findAll();
        verifyNoInteractions(trainingTypeMapper);
    }
}