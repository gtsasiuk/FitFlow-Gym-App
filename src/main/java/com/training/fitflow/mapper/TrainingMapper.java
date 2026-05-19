package com.training.fitflow.mapper;

import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
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

    @Mapping(target = "trainingName", source = "name")
    @Mapping(target = "trainingDate", source = "date")
    @Mapping(target = "trainingType", source = "type.name")
    @Mapping(target = "trainingDuration", source = "duration")
    @Mapping(target = "trainerName", expression = "java(training.getTrainer().getFirstName() + ' ' + training.getTrainer().getLastName())")
    TraineeTrainingResponse toTraineeTrainingResponse(Training training);

    @Mapping(target = "trainingName", source = "name")
    @Mapping(target = "trainingDate", source = "date")
    @Mapping(target = "trainingType", source = "type.name")
    @Mapping(target = "trainingDuration", source = "duration")
    @Mapping(target = "traineeName", expression = "java(training.getTrainee().getFirstName() + ' ' + training.getTrainee().getLastName())")
    TrainerTrainingResponse toTrainerTrainingResponse(Training training);
}
