package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainee.response.TraineeSummaryResponse;
import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;

import java.util.List;

public record TrainerProfileResponse(
        String firstName,
        String lastName,
        SpecializationResponse specialization,
        Boolean isActive,
        List<TraineeSummaryResponse> trainees
) {
}
