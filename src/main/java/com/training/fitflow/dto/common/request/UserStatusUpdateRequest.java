package com.training.fitflow.dto.common.request;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull Boolean isActive
) {
}
