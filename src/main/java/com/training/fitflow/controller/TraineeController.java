package com.training.fitflow.controller;

import com.training.fitflow.dto.common.request.UserStatusUpdateRequest;
import com.training.fitflow.dto.exception.response.ErrorResponse;
import com.training.fitflow.dto.trainee.request.TraineeCreateRequest;
import com.training.fitflow.dto.trainee.request.TraineeUpdateRequest;
import com.training.fitflow.dto.trainee.response.TraineeCreateResponse;
import com.training.fitflow.dto.trainee.response.TraineeProfileResponse;
import com.training.fitflow.dto.trainee.response.TraineeUpdateResponse;
import com.training.fitflow.dto.trainer.request.TraineeTrainersUpdateRequest;
import com.training.fitflow.dto.trainer.response.TrainerSummaryResponse;
import com.training.fitflow.service.TraineeService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Trainee Management",
        description = "Endpoints for managing trainee accounts, profiles, and trainer assignments"
)
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @Operation(
            summary = "Create new trainee account",
            description = """
                Creates a new trainee user in the system.
                Username and password are generated automatically.
                The account is activated by default.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TraineeCreateResponse> createTrainee(@Valid @RequestBody TraineeCreateRequest request) {
        TraineeCreateResponse response = traineeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    @Operation(
            summary = "Get trainee profile",
            description = "Returns full profile information of a trainee including assigned trainers"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile retrieved successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TraineeProfileResponse> getTraineeProfile(@PathVariable("username")
                                                                    @NotBlank(message = "Username is required") String username) {
        TraineeProfileResponse response = traineeService.getByUsername(username);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{username}/unassigned-trainers")
    @Operation(
            summary = "Get available trainers for trainee",
            description = """
                Returns a list of trainers that are NOT assigned to the specified trainee.
                Useful for assigning new trainers.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<TrainerSummaryResponse>> getUnassignedTrainers(@PathVariable("username")
                                                                              @NotBlank(message = "Username is required")
                                                                              String username) {
        List<TrainerSummaryResponse> response = traineeService.getUnassignedTrainers(username);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{username}/status")
    @Operation(
            summary = "Update trainee activation status",
            description = """
                Activates or deactivates trainee account based on provided boolean flag.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid state transition (already active/inactive)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> updateStatus(@PathVariable("username")
                                             @NotBlank(message = "Username is required") String username,
                                             @Valid @RequestBody UserStatusUpdateRequest request) {
        if (request.isActive()) {
            traineeService.activate(username);
        } else {
            traineeService.deactivate(username);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}")
    @Operation(
            summary = "Update trainee profile",
            description = "Updates trainee personal information such as name, birth date, and address"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<TraineeUpdateResponse> updateTraineeProfile(@PathVariable("username")
                                                                      @NotBlank(message = "Username is required")
                                                                      String username,
                                                                      @Valid @RequestBody TraineeUpdateRequest request) {
        TraineeUpdateResponse response = traineeService.update(username, request);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{username}/trainers")
    @Operation(
            summary = "Update trainee trainer list",
            description = """
                Replaces the current list of assigned trainers with a new list.
                All previous assignments will be removed.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer list updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<TrainerSummaryResponse>> updateTraineeProfile(@PathVariable("username")
                                                                             @NotBlank(message = "Username is required")
                                                                             String username,
                                                                             @Valid @RequestBody TraineeTrainersUpdateRequest request) {
        List<TrainerSummaryResponse> response = traineeService.updateTraineeTrainers(username, request);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{username}")
    @Operation(
            summary = "Delete trainee account",
            description = """
                Permanently deletes trainee account from system.
                All related trainer assignments are removed first.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> deleteTrainee(@PathVariable("username")
                                              @NotBlank(message = "Username is required") String username) {
        traineeService.deleteByUsername(username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
