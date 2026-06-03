package com.training.fitflow.security.jwt;

import com.training.fitflow.security.CustomUserDetailsService;
import com.training.fitflow.security.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter Tests")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        userDetails = new User("john.doe", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ───────────────── valid token ─────────────────

    @Test
    @DisplayName("doFilterInternal → valid token → authentication set in context")
    void doFilterInternal_validToken_setsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(tokenBlacklistService.isBlacklisted("valid-token")).thenReturn(false);
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);
        when(jwtTokenProvider.extractUsername("valid-token")).thenReturn("john.doe");
        when(userDetailsService.loadUserByUsername("john.doe")).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("john.doe",
                SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    // ───────────────── blacklisted token ─────────────────

    @Test
    @DisplayName("doFilterInternal → blacklisted token → authentication not set")
    void doFilterInternal_blacklistedToken_noAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer blacklisted-token");
        when(tokenBlacklistService.isBlacklisted("blacklisted-token")).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider);
    }

    // ───────────────── invalid token ─────────────────

    @Test
    @DisplayName("doFilterInternal → invalid token → authentication not set")
    void doFilterInternal_invalidToken_noAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(tokenBlacklistService.isBlacklisted("invalid-token")).thenReturn(false);
        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    // ───────────────── no header ─────────────────

    @Test
    @DisplayName("doFilterInternal → no Authorization header → authentication not set")
    void doFilterInternal_noHeader_noAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenBlacklistService, jwtTokenProvider, userDetailsService);
    }

    // ───────────────── wrong header format ─────────────────

    @Test
    @DisplayName("doFilterInternal → header without Bearer prefix → authentication not set")
    void doFilterInternal_wrongHeaderFormat_noAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic somebase64==");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenBlacklistService, jwtTokenProvider, userDetailsService);
    }
}