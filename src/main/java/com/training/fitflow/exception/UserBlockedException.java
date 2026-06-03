package com.training.fitflow.exception;

public class UserBlockedException extends RuntimeException {
    public UserBlockedException() {
        super("Account temporarily blocked. Try again in 5 minutes.");
    }
}
