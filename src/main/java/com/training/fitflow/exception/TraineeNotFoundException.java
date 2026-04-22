package com.training.fitflow.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(Long id) {
        super("Trainee with id=" + id + " not found");
    }
}
