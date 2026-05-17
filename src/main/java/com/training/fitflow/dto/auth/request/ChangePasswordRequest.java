package com.training.fitflow.dto.auth.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String username,
        @NotBlank String oldPassword,
        @NotBlank String newPassword
) {
}
