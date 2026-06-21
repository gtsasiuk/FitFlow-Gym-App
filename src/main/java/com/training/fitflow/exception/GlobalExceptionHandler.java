package com.training.fitflow.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.fitflow.dto.exception.response.ErrorResponse;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(WorkloadServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleWorkloadUnavailable(WorkloadServiceUnavailableException ex) {
        log.error("Workload service unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidWorkloadQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWorkloadQuery(InvalidWorkloadQueryException ex) {
        log.warn("Invalid workload query: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(FeignException ex) {
        int status = ex.status();

        if (status >= 400 && status < 500) {
            log.warn("Downstream client error: status={}, body={}", status, ex.contentUTF8());
            return ResponseEntity.status(status)
                    .body(new ErrorResponse(extractMessage(ex.contentUTF8())));
        }

        log.error("Downstream server error: status={}", status, ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("Downstream service error"));
    }

    private String extractMessage(String body) {
        try {
            return new ObjectMapper().readTree(body).path("message").asText("Bad request");
        } catch (Exception e) {
            return "Bad request";
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("Constraint violation: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid username or password"));
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeactivated(
            UserDeactivatedException ex) {
        log.warn("User deactivated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("User account is deactivated"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TraineeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTraineeNotFound(
            TraineeNotFoundException ex) {
        log.warn("Trainee not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TrainerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrainerNotFound(
            TrainerNotFoundException ex) {
        log.warn("Trainer not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(TrainingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrainingNotFound(
            TrainingNotFoundException ex) {
        log.warn("Training not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(SpecializationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSpecializationNotFound(
            SpecializationNotFoundException ex) {
        log.warn("Training type not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex) {
        log.warn("No handler found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Endpoint not found: " + ex.getHttpMethod() + " " + ex.getRequestURL()));
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlocked(UserBlockedException ex) {
        log.warn("User blocked: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse(ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
    }
}
