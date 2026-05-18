package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;

public record TrainerSummaryResponse(
        String username,
        String firstName,
        String lastName,
        SpecializationResponse specialization
) {
}
