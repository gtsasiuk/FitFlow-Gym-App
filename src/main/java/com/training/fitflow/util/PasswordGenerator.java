package com.training.fitflow.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PasswordGenerator {
    public String generate() {
        String password = UUID.randomUUID().toString().substring(0, 10);
        log.debug("Password generated (masked)");
        return password;
    }
}
