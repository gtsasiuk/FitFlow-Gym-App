package com.training.fitflow.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtTokenProvider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-that-is-long-enough-for-hmac-sha");
        props.setExpiration(3600000L); // 1 hour

        jwtTokenProvider = new JwtTokenProvider(props);
        jwtTokenProvider.init();
    }

    // ───────────────── generateToken ─────────────────

    @Test
    @DisplayName("generateToken → valid username → returns non-null token")
    void generateToken_validUsername_returnsToken() {
        String token = jwtTokenProvider.generateToken("john.doe");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    // ───────────────── extractUsername ─────────────────

    @Test
    @DisplayName("extractUsername → valid token → returns correct username")
    void extractUsername_validToken_returnsUsername() {
        String token = jwtTokenProvider.generateToken("john.doe");

        String username = jwtTokenProvider.extractUsername(token);

        assertEquals("john.doe", username);
    }

    // ───────────────── validateToken ─────────────────

    @Test
    @DisplayName("validateToken → valid token → returns true")
    void validateToken_validToken_returnsTrue() {
        String token = jwtTokenProvider.generateToken("john.doe");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("validateToken → expired token → returns false")
    void validateToken_expiredToken_returnsFalse() {
        JwtProperties expiredProps = new JwtProperties();
        expiredProps.setSecret("test-secret-key-that-is-long-enough-for-hmac-sha");
        expiredProps.setExpiration(-1000L); // вже протухлий

        JwtTokenProvider expiredProvider = new JwtTokenProvider(expiredProps);
        expiredProvider.init();

        String token = expiredProvider.generateToken("john.doe");

        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("validateToken → malformed token → returns false")
    void validateToken_malformedToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("not.a.valid.token"));
    }

    @Test
    @DisplayName("validateToken → wrong signature → returns false")
    void validateToken_wrongSignature_returnsFalse() {
        JwtProperties otherProps = new JwtProperties();
        otherProps.setSecret("completely-different-secret-key-long-enough-!!!");
        otherProps.setExpiration(3600000L);

        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProps);
        otherProvider.init();

        String tokenFromOtherKey = otherProvider.generateToken("john.doe");

        assertFalse(jwtTokenProvider.validateToken(tokenFromOtherKey));
    }

    @Test
    @DisplayName("validateToken → empty string → returns false")
    void validateToken_emptyToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }
}