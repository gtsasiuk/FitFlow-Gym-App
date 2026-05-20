package com.training.fitflow.dto.trainee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TraineeUpdateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        LocalDate dateOfBirth,
        String address,
        @NotNull(message = "isActive must not be null") Boolean isActive
) {
}
