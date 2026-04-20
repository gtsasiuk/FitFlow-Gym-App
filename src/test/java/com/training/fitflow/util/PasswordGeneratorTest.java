package com.training.fitflow.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordGenerator Tests")
class PasswordGeneratorTest {

    private final PasswordGenerator generator = new PasswordGenerator();

    @Test
    @DisplayName("Generate → should return non-null password of length 10")
    void generate_shouldReturnPasswordWithLength10() {
        String result = generator.generate();

        assertNotNull(result);
        assertEquals(10, result.length());
    }

    @Test
    @DisplayName("Generate → should generate different passwords each time")
    void generate_shouldReturnDifferentPasswords() {
        String p1 = generator.generate();
        String p2 = generator.generate();

        assertNotEquals(p1, p2);
    }
}