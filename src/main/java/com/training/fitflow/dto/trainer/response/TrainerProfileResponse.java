package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainee.response.TraineeSummaryResponse;
import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Full trainer profile information")
public record TrainerProfileResponse(
        String firstName,
        String lastName,
        @Schema(description = "Trainer specialization details")
        SpecializationResponse specialization,
        Boolean isActive,
        @Schema(description = "List of trainees assigned to trainer")
        List<TraineeSummaryResponse> trainees
) {
}
