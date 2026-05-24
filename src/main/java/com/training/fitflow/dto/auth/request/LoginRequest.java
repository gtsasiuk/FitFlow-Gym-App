package com.training.fitflow.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "LoginRequest",
        description = "Data transfer object used for user authentication (login process)"
)
public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Schema(
                description = "Unique username of Trainee or Trainer account",
                example = "john.doe",
                minLength = 3,
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String username,

        @NotBlank(message = "Password is required")
        @Schema(
                description = "User account password",
                example = "SecurePass123!",
                minLength = 6,
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED,
                format = "password"
        )
        String password
) {}