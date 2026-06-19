package com.training.fitflow.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object used for user authentication response (login process)")
public record LoginResponse(
        @Schema(description = "JWT token issued upon successful authentication, used for subsequent requests to protected endpoints")
        String token
) {
}
