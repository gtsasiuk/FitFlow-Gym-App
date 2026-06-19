package com.training.fitflow.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BruteForceProtectionService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_DURATION_MS = 5 * 60 * 1000L;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();

    public void registerFailure(String username) {
        int current = attempts.merge(username, 1, Integer::sum);
        log.warn("Failed login attempt #{} for username={}", current, username);

        if (current >= MAX_ATTEMPTS) {
            long unblockTime = System.currentTimeMillis() + BLOCK_DURATION_MS;
            blockedUntil.put(username, unblockTime);
            log.warn("User blocked for 5 minutes username={}", username);
        }
    }

    public boolean isBlocked(String username) {
        Long unblockTime = blockedUntil.get(username);

        if (unblockTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > unblockTime) {
            blockedUntil.remove(username);
            attempts.remove(username);
            log.info("User unblocked username={}", username);
            return false;
        }

        return true;
    }

    public void resetAttempts(String username) {
        attempts.remove(username);
        blockedUntil.remove(username);
        log.debug("Login attempts reset for username={}", username);
    }
}
