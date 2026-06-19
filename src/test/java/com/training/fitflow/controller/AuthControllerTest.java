package com.training.fitflow.controller;

import com.training.fitflow.dto.auth.request.ChangePasswordRequest;
import com.training.fitflow.dto.auth.request.LoginRequest;
import com.training.fitflow.dto.auth.response.LoginResponse;
import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserBlockedException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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

    // ───────────────── login ─────────────────

    @Test
    @DisplayName("login → valid request → returns 200 with token")
    void login_validRequest_returns200WithToken() {
        LoginRequest request = new LoginRequest("john.doe", "pass123");
        when(authService.login("john.doe", "pass123")).thenReturn("jwt-token");

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().token());

        verify(authService).login("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → bad credentials → throws BadCredentialException")
    void login_badCredentials_throwsBadCredentialException() {
        LoginRequest request = new LoginRequest("john.doe", "wrong");
        doThrow(new BadCredentialException("john.doe"))
                .when(authService).login("john.doe", "wrong");

        assertThrows(BadCredentialException.class,
                () -> authController.login(request)
        );

        verify(authService).login("john.doe", "wrong");
    }

    @Test
    @DisplayName("login → deactivated user → throws UserDeactivatedException")
    void login_deactivatedUser_throwsUserDeactivatedException() {
        LoginRequest request = new LoginRequest("john.doe", "pass123");
        doThrow(new UserDeactivatedException("john.doe"))
                .when(authService).login("john.doe", "pass123");

        assertThrows(UserDeactivatedException.class,
                () -> authController.login(request)
        );

        verify(authService).login("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → user blocked → throws UserBlockedException")
    void login_userBlocked_throwsUserBlockedException() {
        LoginRequest request = new LoginRequest("john.doe", "pass123");
        doThrow(new UserBlockedException())
                .when(authService).login("john.doe", "pass123");

        assertThrows(UserBlockedException.class,
                () -> authController.login(request)
        );

        verify(authService).login("john.doe", "pass123");
    }

    @Test
    @DisplayName("login → service called exactly once")
    void login_serviceCalledExactlyOnce() {
        LoginRequest request = new LoginRequest("john.doe", "pass123");
        when(authService.login("john.doe", "pass123")).thenReturn("jwt-token");

        authController.login(request);

        verify(authService, times(1)).login("john.doe", "pass123");
        verifyNoMoreInteractions(authService);
    }

    // ───────────────── logout ─────────────────

    @Test
    @DisplayName("logout → valid Bearer token → returns 200 and blacklists token")
    void logout_validBearerToken_returns200() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");

        ResponseEntity<Void> response = authController.logout(request);

        assertEquals(200, response.getStatusCode().value());
        verify(authService).logout("jwt-token");
    }

    @Test
    @DisplayName("logout → no Authorization header → returns 200 without calling service")
    void logout_noHeader_returns200WithoutCallingService() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        ResponseEntity<Void> response = authController.logout(request);

        assertEquals(200, response.getStatusCode().value());
        verify(authService, never()).logout(any());
    }

    @Test
    @DisplayName("logout → invalid header format → returns 200 without calling service")
    void logout_invalidHeaderFormat_returns200WithoutCallingService() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        ResponseEntity<Void> response = authController.logout(request);

        assertEquals(200, response.getStatusCode().value());
        verify(authService, never()).logout(any());
    }

    // ───────────────── changePassword ─────────────────

    @Test
    @DisplayName("changePassword → valid request → returns 200 OK")
    void changePassword_validRequest_returns200() {
        ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "oldPass", "newPass");

        ResponseEntity<Void> response = authController.changePassword(request);

        assertEquals(200, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(authService).changePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    @DisplayName("changePassword → bad credentials → throws BadCredentialException")
    void changePassword_badCredentials_throwsBadCredentialException() {
        ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "wrongOld", "newPass");
        doThrow(new BadCredentialException("john.doe"))
                .when(authService).changePassword("john.doe", "wrongOld", "newPass");

        assertThrows(BadCredentialException.class,
                () -> authController.changePassword(request)
        );

        verify(authService).changePassword("john.doe", "wrongOld", "newPass");
    }

    @Test
    @DisplayName("changePassword → unexpected exception → propagates exception")
    void changePassword_unexpectedException_propagatesException() {
        ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "oldPass", "newPass");
        doThrow(new RuntimeException("Unexpected error"))
                .when(authService).changePassword("john.doe", "oldPass", "newPass");

        assertThrows(RuntimeException.class,
                () -> authController.changePassword(request)
        );

        verify(authService).changePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    @DisplayName("changePassword → service called exactly once")
    void changePassword_serviceCalledExactlyOnce() {
        ChangePasswordRequest request = new ChangePasswordRequest("john.doe", "oldPass", "newPass");

        authController.changePassword(request);

        verify(authService, times(1)).changePassword("john.doe", "oldPass", "newPass");
        verifyNoMoreInteractions(authService);
    }
}