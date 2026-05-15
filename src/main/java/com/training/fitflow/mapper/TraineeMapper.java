package com.training.fitflow.mapper;

import com.training.fitflow.dto.request.TraineeCreateRequest;
import com.training.fitflow.dto.response.TraineeCreateResponse;
import com.training.fitflow.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    Trainee toEntity(TraineeCreateRequest traineeCreateRequest);
    TraineeCreateResponse toTraineeCreateResponse(Trainee trainee);
}
