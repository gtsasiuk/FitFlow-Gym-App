package com.training.fitflow.dto.training.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Training information from trainee perspective")
public record TraineeTrainingResponse(
        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Integer trainingDuration,
        String trainerName
) {
}
