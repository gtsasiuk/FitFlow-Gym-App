package com.training.fitflow.dto.training.response;

import java.time.LocalDate;

public record TrainerTrainingResponse(
        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Integer trainingDuration,
        String traineeName
) {
}
