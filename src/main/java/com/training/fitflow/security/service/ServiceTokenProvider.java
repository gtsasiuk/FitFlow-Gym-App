package com.training.fitflow.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ServiceTokenProvider {
    @Value("${service.jwt.secret}")
    private String secret;
    @Value("${service.jwt.expiration}")
    private long expirationMs;

    private volatile String cachedToken;
    private volatile long expiresAt;

    public String generateToken() {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject("fitflow-main-service")
                .claim("scope", "service")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public synchronized String getToken() {
        long now = System.currentTimeMillis();
        if (cachedToken == null || now >= expiresAt - 30_000) {
            cachedToken = generateToken();
            expiresAt = now + expirationMs;
        }
        return cachedToken;
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
