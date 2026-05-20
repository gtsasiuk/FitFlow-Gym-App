package com.training.fitflow.security;

import com.training.fitflow.exception.BadCredentialException;
import com.training.fitflow.exception.UserDeactivatedException;
import com.training.fitflow.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class BasicAuthInterceptor implements HandlerInterceptor {
    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isPublicEndpoint(path, method)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Missing Authorization header");
            return false;
        }

        try {
            String base64 = authHeader.substring("Basic ".length());
            String credentials = new String(
                    Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
            String[] parts = credentials.split(":", 2);

            authService.authenticate(parts[0], parts[1]);
            return true;

        } catch (UserDeactivatedException e) {
            log.warn("User is deactivated: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is deactivated");
            return false;
        } catch (BadCredentialException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
            return false;
        } catch (Exception e) {
            log.error("Unexpected auth error: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Auth error");
            return false;
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        return (path.endsWith("/api/v1/trainees") && method.equals("POST"))
                || (path.endsWith("/api/v1/trainers") && method.equals("POST"))
                || (path.contains("/api/v1/auth/login") && method.equals("POST"));
    }
}