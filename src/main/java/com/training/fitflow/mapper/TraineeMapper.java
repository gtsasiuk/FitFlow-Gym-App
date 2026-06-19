package com.training.fitflow.mapper;

import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    Trainee toEntity(TraineeCreateRequest traineeCreateRequest);

    default TraineeCreateResponse toTraineeCreateResponse(Trainee trainee, String rawPassword) {
        return new TraineeCreateResponse(trainee.getUsername(), rawPassword);
    }

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "trainers", source = "trainers")
    TraineeProfileResponse toProfileResponse(Trainee trainee);

    @Mapping(target = "isActive", source = "active")
    TraineeUpdateResponse toTraineeUpdateResponse(Trainee trainee);
}
