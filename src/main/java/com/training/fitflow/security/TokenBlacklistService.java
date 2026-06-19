package com.training.fitflow.security;

import com.training.fitflow.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {
    private final JwtTokenProvider jwtTokenProvider;
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void blacklist(String token) {
        blacklist.add(token);
        log.info("Token added to blacklist");
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanExpiredTokens() {
        int before = blacklist.size();
        blacklist.removeIf(token -> !jwtTokenProvider.validateToken(token));
        log.debug("Blacklist cleanup: removed {} expired tokens, remaining {}",
                before - blacklist.size(), blacklist.size());
    }
}
