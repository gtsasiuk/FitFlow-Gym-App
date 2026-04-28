package com.training.fitflow.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(String username) {
        super("Trainee with username=" + username + " not found");
    }
}
