package com.training.fitflow.exception;

public class UserDeactivatedException extends RuntimeException {
    public UserDeactivatedException(String username) {
        super(username);
    }
}
