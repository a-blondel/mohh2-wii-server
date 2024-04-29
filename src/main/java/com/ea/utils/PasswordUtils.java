package com.ea.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean matches(String password, String dbPassword) {
        return passwordEncoder.matches(password, dbPassword);
    }

    /**
     * Truncate a string at the first null terminator (0x00 - '\0')
     * @param input The input string
     * @return The truncated string
     */
    public String truncateAtNull(String input) {
        int nullPos = input.indexOf('\0');
        return (nullPos != -1) ? input.substring(0, nullPos) : input;
    }

    /**
     * Sanitize input by removing enclosing quotes and leading tilde
     * @param input The input string
     * @return The sanitized string
     */
    public String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Remove enclosing quotes
        if (input.charAt(0) == 0x22 && input.charAt(input.length() - 1) == 0x22) {
            input = input.substring(1, input.length() - 1);
        }

        // Remove leading tilde
        if (input.charAt(0) == 0x7E) {
            input = input.substring(1);
        }

        return input;
    }


}
