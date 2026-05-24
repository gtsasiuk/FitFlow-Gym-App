package com.training.fitflow.controller;

import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.exception.response.ErrorResponse;
import com.training.fitflow.dto.trainer.request.TrainerCreateRequest;
import com.training.fitflow.dto.trainer.request.TrainerUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerCreateResponse;
import com.training.fitflow.dto.trainer.response.TrainerProfileResponse;
import com.training.fitflow.dto.trainer.response.TrainerUpdateResponse;
import com.training.fitflow.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Trainer Management",
        description = "Endpoints for managing trainer accounts, profiles, and their assigned trainees"
)
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @Operation(
            summary = "Create new trainer account",
            description = """
                Creates a new trainer account in the system.
                Username and password are generated automatically.
                Trainer must be assigned to a specialization.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TrainerCreateResponse> createTrainer(@Valid @RequestBody TrainerCreateRequest request) {
        TrainerCreateResponse response = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get trainer profile",
            description = """
                Returns full trainer profile including specialization and assigned trainees.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile retrieved successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TrainerProfileResponse> getTrainerProfile(@PathVariable("username")
                                                                    @NotBlank(message = "Username is required") String username) {
        TrainerProfileResponse response = trainerService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{username}/status")
    @Operation(
            summary = "Update trainer activation status",
            description = """
                Activates or deactivates trainer account based on provided flag.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid state transition",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> updateStatus(@PathVariable("username")
                                             @NotBlank(message = "Username is required") String username,
                                             @Valid @RequestBody UserStatusUpdateRequest request) {
        if (request.isActive()) {
            trainerService.activate(username);
        } else {
            trainerService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}")
    @Operation(
            summary = "Update trainer profile",
            description = """
                Updates trainer personal data including name and activation status.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TrainerUpdateResponse> updateTrainerProfile(@PathVariable("username")
                                                                   @NotBlank(message = "Username is required") String username,
                                                                   @Valid @RequestBody TrainerUpdateRequest request) {
        TrainerUpdateResponse response = trainerService.update(username, request);
        return ResponseEntity.ok().body(response);
    }
}
