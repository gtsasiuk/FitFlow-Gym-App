package com.training.fitflow.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeCreateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        LocalDate birthDate,
        String address
) {
}
