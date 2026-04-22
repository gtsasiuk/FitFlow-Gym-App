package com.training.fitflow.exception;

public class TrainingNotFoundException extends RuntimeException {
    public TrainingNotFoundException(Long id) {
        super("Training with id=" + id + " not found");
    }
}
