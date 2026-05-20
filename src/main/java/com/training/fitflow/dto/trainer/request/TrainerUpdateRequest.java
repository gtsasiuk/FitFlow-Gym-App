package com.training.fitflow.dto.trainer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for updating trainer profile")
public record TrainerUpdateRequest(
        @NotBlank(message = "First name is required")
        @Schema(example = "John")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Schema(example = "Smith")
        String lastName,
        @NotNull(message = "isActive must not be null")
        @Schema(description = "Trainer active status", example = "true")
        Boolean isActive
) {
}
