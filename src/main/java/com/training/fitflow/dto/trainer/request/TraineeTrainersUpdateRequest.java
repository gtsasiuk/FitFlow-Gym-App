package com.training.fitflow.dto.trainer.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TraineeTrainersUpdateRequest(
        @NotNull List<@NotNull String> trainerUsernames
) {
}
