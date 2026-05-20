package com.training.fitflow.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(
        name = "ChangePasswordRequest",
        description = "DTO used for updating user password with old password verification"
)
public record ChangePasswordRequest(

        @NotBlank
        @Schema(
                description = "Username of the account",
                example = "john.doe",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String username,

        @NotBlank
        @Schema(
                description = "Current (old) password used for verification",
                example = "OldPass123!",
                format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String oldPassword,

        @NotBlank
        @Schema(
                description = "New password that will replace the old one",
                example = "NewStrongPass456!",
                minLength = 8,
                maxLength = 100,
                format = "password",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String newPassword
) {}