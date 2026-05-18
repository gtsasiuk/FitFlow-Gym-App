package com.training.fitflow.dto.trainer.response;

import com.training.fitflow.dto.trainee.response.TraineeSummaryResponse;

import java.util.List;

public record TrainerProfileResponse(
        String firstName,
        String lastName,
        Long specializationId,
        String specializationName,
        Boolean isActive,
        List<TraineeSummaryResponse> trainees
) {
}
