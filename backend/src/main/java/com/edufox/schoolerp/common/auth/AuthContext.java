package com.edufox.schoolerp.common.auth;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

/** Centralized extraction of the claims already issued by the login flow. */
public final class AuthContext {
    private AuthContext() {
    }

    public static Claims claims(Authentication authentication) {
        return (Claims) authentication.getDetails();
    }

    public static String value(Authentication authentication, String key) {
        Object value = claims(authentication).get(key);
        return value == null ? "" : String.valueOf(value);
    }

    public static Integer userId(Authentication authentication) {
        try {
            String value = value(authentication, "userId");
            return value.isBlank() ? null : Integer.valueOf(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
