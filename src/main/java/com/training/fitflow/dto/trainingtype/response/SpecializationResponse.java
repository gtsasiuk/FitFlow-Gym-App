package com.training.fitflow.dto.trainingtype.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trainer specialization data")
public record SpecializationResponse(
        @Schema(example = "1")
        Long id,
        @Schema(example = "Fitness")
        String name
) {
}
