package com.training.fitflow.dto.trainer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerCreateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Long specializationId
) {
}
