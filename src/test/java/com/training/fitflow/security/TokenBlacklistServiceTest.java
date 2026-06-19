package com.training.fitflow.security;

import com.training.fitflow.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistService Tests")
class TokenBlacklistServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    // ───────────────── blacklist / isBlacklisted ─────────────────

    @Test
    @DisplayName("isBlacklisted → token not blacklisted → returns false")
    void isBlacklisted_tokenNotBlacklisted_returnsFalse() {
        assertFalse(tokenBlacklistService.isBlacklisted("some-token"));
    }

    @Test
    @DisplayName("blacklist → token added → isBlacklisted returns true")
    void blacklist_tokenAdded_isBlacklistedReturnsTrue() {
        tokenBlacklistService.blacklist("jwt-token");

        assertTrue(tokenBlacklistService.isBlacklisted("jwt-token"));
    }

    @Test
    @DisplayName("blacklist → multiple tokens → each individually tracked")
    void blacklist_multipleTokens_eachTrackedIndependently() {
        tokenBlacklistService.blacklist("token-one");
        tokenBlacklistService.blacklist("token-two");

        assertTrue(tokenBlacklistService.isBlacklisted("token-one"));
        assertTrue(tokenBlacklistService.isBlacklisted("token-two"));
        assertFalse(tokenBlacklistService.isBlacklisted("token-three"));
    }

    // ───────────────── cleanExpiredTokens ─────────────────

    @Test
    @DisplayName("cleanExpiredTokens → expired tokens → removed from blacklist")
    void cleanExpiredTokens_expiredTokens_removedFromBlacklist() {
        tokenBlacklistService.blacklist("expired-token");
        tokenBlacklistService.blacklist("valid-token");

        when(jwtTokenProvider.validateToken("expired-token")).thenReturn(false);
        when(jwtTokenProvider.validateToken("valid-token")).thenReturn(true);

        tokenBlacklistService.cleanExpiredTokens();

        assertFalse(tokenBlacklistService.isBlacklisted("expired-token"));
        assertTrue(tokenBlacklistService.isBlacklisted("valid-token"));
    }

    @Test
    @DisplayName("cleanExpiredTokens → all tokens valid → nothing removed")
    void cleanExpiredTokens_allValid_nothingRemoved() {
        tokenBlacklistService.blacklist("token-one");
        tokenBlacklistService.blacklist("token-two");

        when(jwtTokenProvider.validateToken(any())).thenReturn(true);

        tokenBlacklistService.cleanExpiredTokens();

        assertTrue(tokenBlacklistService.isBlacklisted("token-one"));
        assertTrue(tokenBlacklistService.isBlacklisted("token-two"));
    }

    @Test
    @DisplayName("cleanExpiredTokens → empty blacklist → no interactions with provider")
    void cleanExpiredTokens_emptyBlacklist_noProviderInteractions() {
        tokenBlacklistService.cleanExpiredTokens();

        verifyNoInteractions(jwtTokenProvider);
    }
}