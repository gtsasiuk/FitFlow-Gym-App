package com.training.fitflow.dto.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard error response structure for API failures")
public record ErrorResponse(
        @Schema(description = "Detailed error message explaining the failure", example = "Invalid username or password")
        String message
) {
}