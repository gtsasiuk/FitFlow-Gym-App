package com.training.fitflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicAuthInterceptor Tests")
class BasicAuthInterceptorTest {

    @Mock
    private AuthService authService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private BasicAuthInterceptor interceptor;

    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    // ─── public endpoints ─────────────────────────────────────────────

    @Test
    @DisplayName("preHandle → public trainee endpoint → returns true")
    void preHandle_publicTraineeEndpoint_returnsTrue() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/trainees");
        when(request.getMethod()).thenReturn("POST");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);

        verifyNoInteractions(authService);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    @DisplayName("preHandle → public trainer endpoint → returns true")
    void preHandle_publicTrainerEndpoint_returnsTrue() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/trainers");
        when(request.getMethod()).thenReturn("POST");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("preHandle → public login endpoint → returns true")
    void preHandle_publicLoginEndpoint_returnsTrue() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/auth/login");
        when(request.getMethod()).thenReturn("POST");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);

        verifyNoInteractions(authService);
    }

    // ─── missing/invalid authorization header ────────────────────────

    @Test
    @DisplayName("preHandle → missing authorization header → unauthorized")
    void preHandle_missingAuthorizationHeader_returnsUnauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Missing Authorization header"
        );

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("preHandle → invalid authorization header → unauthorized")
    void preHandle_invalidAuthorizationHeader_returnsUnauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);

        verify(response).sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Missing Authorization header"
        );

        verifyNoInteractions(authService);
    }

    // ─── successful authentication ───────────────────────────────────

    @Test
    @DisplayName("preHandle → valid credentials → returns true")
    void preHandle_validCredentials_returnsTrue() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");

        String credentials = "john:pass123";
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);

        verify(authService).authenticate("john", "pass123");
    }

    // ─── exceptions ──────────────────────────────────────────────────

    @Test
    @DisplayName("preHandle → deactivated user → forbidden")
    void preHandle_deactivatedUser_returnsForbidden() throws Exception {

        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"message\":\"error\"}");

        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");

        String credentials = "john:pass123";
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        doThrow(new UserDeactivatedException("User disabled"))
                .when(authService)
                .authenticate("john", "pass123");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();

        assertEquals("{\"message\":\"error\"}", stringWriter.toString());
    }

    @Test
    @DisplayName("preHandle → bad credentials → unauthorized")
    void preHandle_badCredentials_returnsUnauthorized() throws Exception {

        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"message\":\"error\"}");

        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");

        String credentials = "john:wrong";
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        doThrow(new BadCredentialException("Invalid credentials"))
                .when(authService)
                .authenticate("john", "wrong");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();

        assertEquals("{\"message\":\"error\"}", stringWriter.toString());
    }

    @Test
    @DisplayName("preHandle → unexpected exception → internal server error")
    void preHandle_unexpectedException_returnsInternalServerError() throws Exception {

        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"message\":\"error\"}");

        when(request.getRequestURI()).thenReturn("/api/v1/secure");
        when(request.getMethod()).thenReturn("GET");

        String credentials = "john:pass123";
        String encoded = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        when(request.getHeader("Authorization"))
                .thenReturn("Basic " + encoded);

        doThrow(new RuntimeException("Unexpected error"))
                .when(authService)
                .authenticate("john", "pass123");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        printWriter.flush();

        assertEquals("{\"message\":\"error\"}", stringWriter.toString());
    }
}