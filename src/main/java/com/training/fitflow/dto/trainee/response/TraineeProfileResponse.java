package com.training.fitflow.dto.trainee.response;

import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileResponse(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        Boolean isActive,
        List<TrainerSummaryResponse> trainers
) {
}
