package com.training.fitflow.mapper;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.model.TrainingType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    SpecializationResponse toResponse(TrainingType trainingType);
}
