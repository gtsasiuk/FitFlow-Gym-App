package com.training.fitflow.util;

import com.training.fitflow.model.User;

public class UserUpdateUtil {

    public static void updateNameFields(User existing, String newFirstName, String newLastName, UsernameGenerator usernameGenerator) {
        boolean nameChanged = !existing.getFirstName().equals(newFirstName) ||
                !existing.getLastName().equals(newLastName);
        existing.setFirstName(newFirstName);
        existing.setLastName(newLastName);
        if (nameChanged) {
            String newUsername = usernameGenerator.generate(newFirstName, newLastName);
            existing.setUsername(newUsername);
        }
    }
}
