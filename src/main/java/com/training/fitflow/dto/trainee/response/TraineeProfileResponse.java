package com.training.fitflow.dto.trainee.response;

import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Full trainee profile information")
public record TraineeProfileResponse(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String address,
        Boolean isActive,
        List<TrainerSummaryResponse> trainers
) {}
