package com.training.fitflow.dto.trainer.response;

public record TrainerSummaryResponse(
        String username,
        String firstName,
        String lastName,
        Long specializationId,
        String specializationName
) {
}
