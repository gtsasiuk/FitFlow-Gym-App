package com.training.fitflow.controller;

import com.training.fitflow.dto.auth.request.ChangePasswordRequest;
import com.training.fitflow.dto.auth.request.LoginRequest;
import com.training.fitflow.dto.auth.response.LoginResponse;
import com.training.fitflow.dto.exception.response.ErrorResponse;
import com.training.fitflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Authentication and security management endpoints for Trainee and Trainer users"
)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = """
                    Authenticates a user (Trainee or Trainer) using username and password.
                    If credentials are valid and account is active, access is granted.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload or validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User account is deactivated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.username(), request.password());
        return ResponseEntity.ok().body(new LoginResponse(token));
    }

    @PutMapping("/change-password")
    @Operation(
            summary = "Change user password",
            description = """
                    Allows authenticated user to change password by providing current password verification.
                    Username must exist and old password must be correct.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password successfully updated"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error in request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed due to invalid credentials",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid password state or business rule violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(
                request.username(),
                request.oldPassword(),
                request.newPassword()
        );
        return ResponseEntity.ok().build();
    }
}