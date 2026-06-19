package com.training.fitflow.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BruteForceProtectionService Tests")
class BruteForceProtectionServiceTest {

    private BruteForceProtectionService service;

    @BeforeEach
    void setUp() {
        service = new BruteForceProtectionService();
    }

    // ───────────────── isBlocked ─────────────────

    @Test
    @DisplayName("isBlocked → no failures registered → returns false")
    void isBlocked_noFailures_returnsFalse() {
        assertFalse(service.isBlocked("john.doe"));
    }

    @Test
    @DisplayName("isBlocked → less than 3 failures → returns false")
    void isBlocked_twoFailures_returnsFalse() {
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertFalse(service.isBlocked("john.doe"));
    }

    @Test
    @DisplayName("isBlocked → 3 failures → returns true")
    void isBlocked_threeFailures_returnsTrue() {
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertTrue(service.isBlocked("john.doe"));
    }

    @Test
    @DisplayName("isBlocked → block expired → returns false and cleans state")
    void isBlocked_blockExpired_returnsFalse() throws Exception {
        // блокуємо юзера
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertTrue(service.isBlocked("john.doe"));

        // вручну проставляємо час в минулому через reflection
        var blockedUntilField = BruteForceProtectionService.class
                .getDeclaredField("blockedUntil");
        blockedUntilField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var blockedUntil = (java.util.Map<String, Long>) blockedUntilField.get(service);
        blockedUntil.put("john.doe", System.currentTimeMillis() - 1000);

        assertFalse(service.isBlocked("john.doe"));
    }

    // ───────────────── registerFailure ─────────────────

    @Test
    @DisplayName("registerFailure → exactly 3 attempts → user gets blocked")
    void registerFailure_thirdAttempt_userBlocked() {
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertFalse(service.isBlocked("john.doe"));

        service.registerFailure("john.doe");

        assertTrue(service.isBlocked("john.doe"));
    }

    @Test
    @DisplayName("registerFailure → different users → independent counters")
    void registerFailure_differentUsers_independentCounters() {
        service.registerFailure("user.one");
        service.registerFailure("user.one");
        service.registerFailure("user.one");

        assertTrue(service.isBlocked("user.one"));
        assertFalse(service.isBlocked("user.two"));
    }

    // ───────────────── resetAttempts ─────────────────

    @Test
    @DisplayName("resetAttempts → after block → user unblocked")
    void resetAttempts_afterBlock_userUnblocked() {
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertTrue(service.isBlocked("john.doe"));

        service.resetAttempts("john.doe");

        assertFalse(service.isBlocked("john.doe"));
    }

    @Test
    @DisplayName("resetAttempts → after reset failures accumulate again")
    void resetAttempts_afterReset_failuresAccumulateAgain() {
        service.registerFailure("john.doe");
        service.registerFailure("john.doe");
        service.resetAttempts("john.doe");

        service.registerFailure("john.doe");
        service.registerFailure("john.doe");

        assertFalse(service.isBlocked("john.doe"));
    }
}