package com.training.fitflow.dto.trainer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request for updating trainee trainer assignments")
public record TraineeTrainersUpdateRequest(
        @NotNull(message = "Trainers list must not be null")
        @NotEmpty(message = "Trainers list must not be empty")
        @Schema(description = "List of trainer usernames", example = "[\"trainer1\", \"trainer2\"]")
        List<@NotBlank(message = "Trainer username must not be blank") String> trainerUsernames
) {
}
