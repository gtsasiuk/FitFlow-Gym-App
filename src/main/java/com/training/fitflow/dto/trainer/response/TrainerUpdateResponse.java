package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainee.response.TraineeSummaryResponse;
import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response after updating trainer profile")
public record TrainerUpdateResponse(
        String username,
        String firstName,
        String lastName,
        SpecializationResponse specialization,
        Boolean isActive,
        List<TraineeSummaryResponse> trainees
) {
}
