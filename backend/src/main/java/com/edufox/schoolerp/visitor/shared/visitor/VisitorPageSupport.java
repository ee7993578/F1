package com.edufox.schoolerp.visitor.shared.visitor;

import com.edufox.schoolerp.common.auth.AuthContext;
import org.springframework.security.core.Authentication;

final class VisitorPageSupport {
    private VisitorPageSupport() {}

    static String school(Authentication authentication) {
        return AuthContext.value(authentication, "schoolId");
    }

    static Integer userId(Authentication authentication) {
        return AuthContext.userId(authentication);
    }

    static Integer intValue(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0 ? number : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
