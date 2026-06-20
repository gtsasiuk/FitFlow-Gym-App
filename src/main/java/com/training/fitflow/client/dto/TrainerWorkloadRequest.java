package com.training.fitflow.client.dto;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean isActive,
        LocalDate trainingDate,
        Long trainingDuration,
        ActionType actionType
) {
    public enum ActionType {
        ADD,
        DELETE
    }
}