package com.training.fitflow.controller;

import com.training.fitflow.dto.exception.response.ErrorResponse;
import com.training.fitflow.dto.trainingtype.response.SpecializationResponse;
import com.training.fitflow.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/training-types")
@RequiredArgsConstructor
@Tag(
        name = "Training Types",
        description = "Endpoint for retrieving available training specializations used for trainers and trainings"
)
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(
            summary = "Get all training types",
            description = """
                Returns a list of all available training specializations.
                These types are used for assigning trainer specialization and training classification.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Training types retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<List<SpecializationResponse>> getAll() {
        return ResponseEntity.ok().body(trainingTypeService.getAll());
    }
}
