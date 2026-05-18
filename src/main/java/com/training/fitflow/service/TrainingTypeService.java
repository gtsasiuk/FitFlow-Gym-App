package com.training.fitflow.service;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.mapper.TrainingTypeMapper;
import com.training.fitflow.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapper trainingTypeMapper;

    public List<SpecializationResponse> getAll() {
        log.debug("Fetching all training types");
        return trainingTypeRepository.findAll()
                .stream()
                .map(trainingTypeMapper::toResponse)
                .toList();
    }
}
