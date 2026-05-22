package com.training.fitflow.controller;

import com.training.fitflow.dto.auth.request.ChangePasswordRequest;
import com.training.fitflow.dto.auth.request.LoginRequest;
import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("login → valid request → returns 200 OK")
    void login_validRequest_returns200() {

        LoginRequest request =
                new LoginRequest("john.doe", "pass123");

        ResponseEntity<Void> response =
                authController.login(request);

        assertNotNull(response);

        assertEquals(200, response.getStatusCode().value());

        assertNull(response.getBody());

        verify(authService)
                .authenticate("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → bad credentials → throws BadCredentialException")
    void login_badCredentials_throwsBadCredentialException() {

        LoginRequest request =
                new LoginRequest("john.doe", "wrong");

        doThrow(new BadCredentialException("john.doe"))
                .when(authService)
                .authenticate("john.doe", "wrong");

        BadCredentialException exception = assertThrows(
                BadCredentialException.class,
                () -> authController.login(request)
        );

        assertEquals(
                "Invalid credentials for user: john.doe",
                exception.getMessage()
        );

        verify(authService)
                .authenticate("john.doe", "wrong");
    }

    @Test
    @DisplayName("login → deactivated user → throws UserDeactivatedException")
    void login_deactivatedUser_throwsUserDeactivatedException() {

        LoginRequest request =
                new LoginRequest("john.doe", "pass123");

        doThrow(new UserDeactivatedException("User is deactivated"))
                .when(authService)
                .authenticate("john.doe", "pass123");

        UserDeactivatedException exception = assertThrows(
                UserDeactivatedException.class,
                () -> authController.login(request)
        );

        assertEquals(
                "User is deactivated",
                exception.getMessage()
        );

        verify(authService)
                .authenticate("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → unexpected service exception → propagates exception")
    void login_unexpectedException_propagatesException() {

        LoginRequest request =
                new LoginRequest("john.doe", "pass123");

        doThrow(new RuntimeException("Unexpected error"))
                .when(authService)
                .authenticate("john.doe", "pass123");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals(
                "Unexpected error",
                exception.getMessage()
        );

        verify(authService)
                .authenticate("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → service called exactly once")
    void login_serviceCalledExactlyOnce() {

        LoginRequest request =
                new LoginRequest("john.doe", "pass123");

        authController.login(request);

        verify(authService, times(1))
                .authenticate("john.doe", "pass123");

        verifyNoMoreInteractions(authService);
    }

    @Test
    @DisplayName("changePassword → valid request → returns 200 OK")
    void changePassword_validRequest_returns200() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        ResponseEntity<Void> response =
                authController.changePassword(request);

        assertNotNull(response);

        assertEquals(200, response.getStatusCode().value());

        assertNull(response.getBody());

        verify(authService)
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );
    }

    @Test
    @DisplayName("changePassword → bad credentials → throws BadCredentialException")
    void changePassword_badCredentials_throwsBadCredentialException() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "john.doe",
                        "wrongOld",
                        "newPass"
                );

        doThrow(new BadCredentialException("john.doe"))
                .when(authService)
                .changePassword(
                        "john.doe",
                        "wrongOld",
                        "newPass"
                );

        BadCredentialException exception = assertThrows(
                BadCredentialException.class,
                () -> authController.changePassword(request)
        );

        assertEquals(
                "Invalid credentials for user: john.doe",
                exception.getMessage()
        );

        verify(authService)
                .changePassword(
                        "john.doe",
                        "wrongOld",
                        "newPass"
                );
    }

    @Test
    @DisplayName("changePassword → deactivated user → throws UserDeactivatedException")
    void changePassword_deactivatedUser_throwsUserDeactivatedException() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        doThrow(new UserDeactivatedException("User is deactivated"))
                .when(authService)
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        UserDeactivatedException exception = assertThrows(
                UserDeactivatedException.class,
                () -> authController.changePassword(request)
        );

        assertEquals(
                "User is deactivated",
                exception.getMessage()
        );

        verify(authService)
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );
    }

    @Test
    @DisplayName("changePassword → unexpected service exception → propagates exception")
    void changePassword_unexpectedException_propagatesException() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        doThrow(new RuntimeException("Unexpected error"))
                .when(authService)
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.changePassword(request)
        );

        assertEquals(
                "Unexpected error",
                exception.getMessage()
        );

        verify(authService)
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );
    }

    @Test
    @DisplayName("changePassword → service called exactly once")
    void changePassword_serviceCalledExactlyOnce() {

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        authController.changePassword(request);

        verify(authService, times(1))
                .changePassword(
                        "john.doe",
                        "oldPass",
                        "newPass"
                );

        verifyNoMoreInteractions(authService);
    }
}