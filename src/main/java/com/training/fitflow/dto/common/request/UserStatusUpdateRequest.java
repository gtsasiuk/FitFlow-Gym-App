package com.training.fitflow.dto.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to change user active status")
public record UserStatusUpdateRequest(
        @NotNull
        @Schema(example = "true", description = "Active status flag")
        Boolean isActive
) {}
