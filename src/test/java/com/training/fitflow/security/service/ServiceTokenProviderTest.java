package com.training.fitflow.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ServiceTokenProvider Tests")
class ServiceTokenProviderTest {
    private ServiceTokenProvider service;

    @BeforeEach
    void setUp() {
        service = new ServiceTokenProvider();

        ReflectionTestUtils.setField(
                service,
                "secret",
                "myVerySecretKeyForJwtSigningThatMustBeAtLeast32Chars"
        );

        ReflectionTestUtils.setField(
                service,
                "expirationMs",
                60_000L
        );
    }

    @Test
    @DisplayName("GenerateToken → should create token")
    void generateToken_shouldCreateToken() {
        String token = service.generateToken();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("GetToken → should cache token")
    void getToken_shouldCacheToken() {
        String token1 = service.getToken();
        String token2 = service.getToken();

        assertEquals(token1, token2);
    }

    @Test
    @DisplayName("GetToken → should refresh expiration when token is expired")
    void getToken_shouldRefreshExpirationWhenExpired() {
        service.getToken();

        ReflectionTestUtils.setField(
                service,
                "expiresAt",
                System.currentTimeMillis() - 1000
        );

        service.getToken();

        Long expiresAt =
                (Long) ReflectionTestUtils.getField(service, "expiresAt");

        assertNotNull(expiresAt);
        assertTrue(expiresAt > System.currentTimeMillis());
    }

    @Test
    @DisplayName("GetToken → should generate token when cache is null")
    void getToken_shouldGenerateWhenCacheIsNull() {
        ReflectionTestUtils.setField(service, "cachedToken", null);

        String token = service.getToken();

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("GetSigningKey → should return secret key")
    void getSigningKey_shouldReturnSecretKey() throws Exception {
        Method method = ServiceTokenProvider.class.getDeclaredMethod("getSigningKey");
        method.setAccessible(true);

        SecretKey key = (SecretKey) method.invoke(service);

        assertNotNull(key);
        assertNotNull(key.getAlgorithm());
        assertTrue(key.getAlgorithm().startsWith("HmacSHA"));
    }
}