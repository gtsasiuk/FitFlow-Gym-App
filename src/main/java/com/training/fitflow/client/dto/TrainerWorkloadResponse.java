package com.training.fitflow.client.dto;

import java.util.Map;

public record TrainerWorkloadResponse(
        String username,
        String firstName,
        String lastName,
        Boolean isActive,
        Map<Integer, Map<Integer, Long>> yearMonthDuration
) {
}
