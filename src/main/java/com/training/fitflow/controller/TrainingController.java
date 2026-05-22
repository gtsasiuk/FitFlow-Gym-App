package com.training.fitflow.controller;

import com.training.fitflow.dto.exception.response.ErrorResponse;
import com.training.fitflow.dto.training.request.TrainingCreateRequest;
import com.training.fitflow.dto.training.response.TraineeTrainingResponse;
import com.training.fitflow.dto.training.response.TrainerTrainingResponse;
import com.training.fitflow.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Training Management",
        description = "Endpoints for creating and retrieving training sessions between trainees and trainers"
)
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @Operation(
            summary = "Create training session",
            description = """
                Creates a new training session between a trainee and a trainer.
                Trainer specialization is automatically used as training type.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training successfully created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer or trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<Void> createTraining(@Valid @RequestBody TrainingCreateRequest request) {
        trainingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/trainee")
    @Operation(
            summary = "Get trainee trainings",
            description = """
                Returns list of training sessions for a specific trainee.
                Supports optional filtering by date range, trainer name, and training type.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<TraineeTrainingResponse>> getTraineeTrainings(
            @RequestParam("username") @NotBlank(message = "Username is required") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "trainerName", required = false) String trainerName,
            @RequestParam(name = "typeId", required = false) Long typeId
    ) {
        List<TraineeTrainingResponse> response = trainingService
                .getTraineeTrainings(username, fromDate, toDate, trainerName, typeId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/trainer")
    @Operation(
            summary = "Get trainer trainings",
            description = """
                Returns list of training sessions for a specific trainer.
                Supports filtering by date range and trainee name.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainerTrainings(
            @RequestParam("username") @NotBlank(message = "Username is required") String username,
            @RequestParam(name = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(name = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(name = "traineeName", required = false) String traineeName
    ) {
        List<TrainerTrainingResponse> response = trainingService
                .getTrainerTrainings(username, fromDate, toDate, traineeName);
        return ResponseEntity.ok().body(response);
    }
}
