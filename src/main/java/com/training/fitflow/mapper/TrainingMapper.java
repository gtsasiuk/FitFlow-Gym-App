package com.training.fitflow.mapper;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "name", source = "trainingName")
    @Mapping(target = "date", source = "trainingDate")
    @Mapping(target = "duration", source = "trainingDuration")
    Training toEntity(TrainingCreateRequest trainingCreateRequest);
}
