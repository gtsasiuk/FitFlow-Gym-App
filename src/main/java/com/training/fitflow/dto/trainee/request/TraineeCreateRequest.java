package com.training.fitflow.dto.trainee.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeCreateRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        LocalDate dateOfBirth,
        String address
) {
}
