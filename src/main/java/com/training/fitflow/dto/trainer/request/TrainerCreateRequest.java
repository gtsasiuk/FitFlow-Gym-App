package com.training.fitflow.dto.trainer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for creating a new trainer account")
public record TrainerCreateRequest(
        @NotBlank(message = "First name is required")
        @Schema(example = "John")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Schema(example = "Smith")
        String lastName,
        @NotNull(message = "Specialization is required")
        @Schema(description = "Specialization ID", example = "1")
        Long specializationId
) {
}
