package com.training.fitflow.mapper;

import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    Trainer toEntity(TrainerCreateRequest trainerCreateRequest);

    default TrainerCreateResponse toCreateResponse(Trainer trainer, String rawPassword) {
        return new TrainerCreateResponse(trainer.getUsername(), rawPassword);
    }

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "trainees", source = "trainees")
    TrainerUpdateResponse toUpdateResponse(Trainer trainer);

    TrainerSummaryResponse toSummaryResponse(Trainer trainer);

    List<TrainerSummaryResponse> toSummaryResponseList(Set<Trainer> trainers);
}
