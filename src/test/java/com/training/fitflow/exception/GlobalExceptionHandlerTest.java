package com.training.fitflow.exception;

import com.training.fitflow.dto.exception.response.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler handler;

    // ─── ConstraintViolationException ────────────────────────────────────────────

    @Test
    @DisplayName("handleConstraintViolation → returns 400 with violation message")
    void handleConstraintViolation_returns400() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        when(violation.getPropertyPath()).thenReturn(PathImpl.createPathFromString("updateStatus.username"));
        when(violation.getMessage()).thenReturn("Username is required");
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));

        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().message().contains("Username is required"));
    }

    // ─── BadCredentialException ───────────────────────────────────────────────

    @Test
    @DisplayName("handleBadCredentials → returns 401 with fixed message")
    void handleBadCredentials_returns401() {
        BadCredentialException ex = new BadCredentialException("john.doe");

        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid username or password", response.getBody().message());
    }

    // ─── UserDeactivatedException ─────────────────────────────────────────────

    @Test
    @DisplayName("handleUserDeactivated → returns 403 with fixed message")
    void handleUserDeactivated_returns403() {
        UserDeactivatedException ex = new UserDeactivatedException("john.doe");

        ResponseEntity<ErrorResponse> response = handler.handleUserDeactivated(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User account is deactivated", response.getBody().message());
    }

    // ─── UserBlockedException ─────────────────────────────────────────────────

    @Test
    @DisplayName("handleUserBlocked → returns 429 with fixed message")
    void handleUserBlocked_returns429() {
        UserBlockedException ex = new UserBlockedException();

        ResponseEntity<ErrorResponse> response = handler.handleUserBlocked(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account temporarily blocked. Try again in 5 minutes.", response.getBody().message());
    }

    // ─── IllegalStateException ────────────────────────────────────────────────

    @Test
    @DisplayName("handleIllegalState → returns 409 with exception message")
    void handleIllegalState_returns409() {
        IllegalStateException ex = new IllegalStateException("Already exists");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Already exists", response.getBody().message());
    }

    // ─── TraineeNotFoundException ─────────────────────────────────────────────

    @Test
    @DisplayName("handleTraineeNotFound → returns 404 with exception message")
    void handleTraineeNotFound_returns404() {
        TraineeNotFoundException ex = new TraineeNotFoundException("john.doe");

        ResponseEntity<ErrorResponse> response = handler.handleTrainerNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Trainee with username=john.doe not found", response.getBody().message());
    }

    // ─── TrainerNotFoundException ─────────────────────────────────────────────

    @Test
    @DisplayName("handleTrainerNotFound → returns 404 with exception message")
    void handleTrainerNotFound_returns404() {
        TrainerNotFoundException ex = new TrainerNotFoundException("jane.doe");

        ResponseEntity<ErrorResponse> response = handler.handleTrainerNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Trainer with username=jane.doe not found", response.getBody().message());
    }

    // ─── TrainingNotFoundException ────────────────────────────────────────────

    @Test
    @DisplayName("handleTrainingNotFound → returns 404 with exception message")
    void handleTrainingNotFound_returns404() {
        TrainingNotFoundException ex = new TrainingNotFoundException(42L);

        ResponseEntity<ErrorResponse> response = handler.handleTrainingNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Training with id=42 not found", response.getBody().message());
    }

    // ─── SpecializationNotFoundException ─────────────────────────────────────

    @Test
    @DisplayName("handleSpecializationNotFound → returns 404 with exception message")
    void handleSpecializationNotFound_returns404() {
        SpecializationNotFoundException ex = new SpecializationNotFoundException("Specialization with id=99 not found");

        ResponseEntity<ErrorResponse> response = handler.handleSpecializationNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Specialization with id=99 not found", response.getBody().message());
    }

    // ─── NoHandlerFoundException ──────────────────────────────────────────────

    @Test
    @DisplayName("handleNoHandlerFound → returns 404 with endpoint info")
    void handleNoHandlerFound_returns404WithEndpointInfo() {
        NoHandlerFoundException ex = new NoHandlerFoundException(
                "GET", "/api/v1/unknown", null
        );

        ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().message().contains("GET"));
        assertTrue(response.getBody().message().contains("/api/v1/unknown"));
    }

    // ─── General Exception ────────────────────────────────────────────────────

    @Test
    @DisplayName("handleGeneral → returns 500 with fixed message")
    void handleGeneral_returns500() {
        Exception ex = new RuntimeException("Something crashed");

        ResponseEntity<ErrorResponse> response = handler.handleGeneral(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().message());
    }

    @Test
    @DisplayName("handleGeneral → hides internal details from response")
    void handleGeneral_hidesInternalDetails() {
        Exception ex = new RuntimeException("DB connection refused at 192.168.1.1:5432");

        ResponseEntity<ErrorResponse> response = handler.handleGeneral(ex);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().message().contains("192.168.1.1"));
        assertEquals("Internal server error", response.getBody().message());
    }

    // ─── MethodArgumentNotValidException ─────────────────────────────────────

    @Test
    @DisplayName("handleMethodArgumentNotValid → returns 400 with field errors joined")
    void handleMethodArgumentNotValid_returns400WithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("obj", "firstName", "must not be blank");
        FieldError fieldError2 = new FieldError("obj", "lastName", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        String message = response.getBody().message();
        assertTrue(message.contains("firstName: must not be blank"));
        assertTrue(message.contains("lastName: must not be blank"));
    }

    @Test
    @DisplayName("handleMethodArgumentNotValid → single field error → correct format")
    void handleMethodArgumentNotValid_singleFieldError_correctFormat() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("obj", "username", "must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("username: must not be blank", response.getBody().message());
    }
}