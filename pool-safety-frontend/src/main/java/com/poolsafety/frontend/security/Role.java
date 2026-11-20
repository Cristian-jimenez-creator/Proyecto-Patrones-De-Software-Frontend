package com.poolsafety.frontend.security;

import java.util.Locale;
import java.util.Optional;

public enum Role {
    ADMIN, SUPERVISOR, OPERATOR;

    public String authority() {
        return "ROLE_" + name();
    }

    /** Accepts "ADMIN" or "ROLE_ADMIN" in any case; unknown values are ignored. */
    public static Optional<Role> fromBackend(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        try {
            return Optional.of(Role.valueOf(normalized));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
