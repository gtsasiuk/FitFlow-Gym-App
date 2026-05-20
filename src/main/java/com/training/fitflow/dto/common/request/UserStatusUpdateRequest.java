package com.training.fitflow.dto.common.request;

import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull(message = "isActive must not be null") Boolean isActive
) {
}
