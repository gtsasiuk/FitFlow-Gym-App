package com.training.fitflow.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TraineeTrainersUpdateRequest(
        @NotNull(message = "Trainers list must not be null")
        @NotEmpty(message = "Trainers list must not be empty")
        List<@NotBlank(message = "Trainer username must not be blank") String> trainerUsernames
) {
}
