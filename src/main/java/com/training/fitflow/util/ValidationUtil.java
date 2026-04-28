package com.training.fitflow.util;

import com.training.fitflow.exception.ValidationException;

public class ValidationUtil {
    public static void notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field + " must not be blank");
        }
    }

    public static void notNull(Object value, String field) {
        if (value == null) {
            throw new ValidationException(field + " must not be null");
        }
    }

    public static void positive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw new ValidationException(field + " must be positive");
        }
    }
}
