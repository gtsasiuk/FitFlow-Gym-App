package com.training.fitflow.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(Long id) {
        super("Trainer with id=" + id + " not found");
    }
}
