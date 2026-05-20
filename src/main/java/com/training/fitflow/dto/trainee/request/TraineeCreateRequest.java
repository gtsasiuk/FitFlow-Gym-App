package com.training.fitflow.dto.trainee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Schema(description = "Request object for creating a new trainee account")
public record TraineeCreateRequest(
        @NotBlank(message = "First name is required")
        @Schema(example = "John")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Schema(example = "Doe")
        String lastName,
        @Schema(example = "1995-05-20")
        LocalDate dateOfBirth,
        @Schema(example = "Kyiv, Ukraine")
        String address
) {
}
