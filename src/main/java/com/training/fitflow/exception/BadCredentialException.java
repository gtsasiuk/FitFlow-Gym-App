package com.training.fitflow.exception;

public class BadCredentialException extends RuntimeException {
    public BadCredentialException(String username) {
        super("Invalid credentials for user: " + username);
    }
}
