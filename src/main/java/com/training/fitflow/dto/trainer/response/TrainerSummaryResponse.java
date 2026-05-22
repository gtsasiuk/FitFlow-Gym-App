package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short trainer representation used in lists")
public record TrainerSummaryResponse(
        String username,
        String firstName,
        String lastName,
        @Schema(description = "Trainer specialization")
        SpecializationResponse specialization
) {
}
