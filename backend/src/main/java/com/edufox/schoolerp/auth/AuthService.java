package com.edufox.schoolerp.auth;

import com.edufox.schoolerp.config.AppProperties;
import com.edufox.schoolerp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Pattern MOBILE = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final AuthRepository repository;
    private final JwtService jwt;
    private final AppProperties properties;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        String identifier = request.identifier().trim();
        String type = MOBILE.matcher(identifier).matches() ? "Number"
            : EMAIL.matcher(identifier).matches() ? "Email" : "invalid";

        if ("invalid".equals(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Enter a valid email or 10-digit mobile number");
        }

        String role = request.role().trim().toLowerCase();
        AuthUser user = repository.findUser(identifier, request.password(), role, type);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid UserId or Password");
        }

        if (user.otpRequired() && properties.getAuth().isOtpEnabled()) {
            if (properties.getAuth().isDatabaseReadOnly()) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Faculty OTP is enabled but the configured database is read-only");
            }
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                "Faculty OTP delivery is not configured yet");
        }

        long seconds = properties.getAuth().getAccessTokenMinutes() * 60;
        return new AuthDtos.LoginResponse(
            jwt.issueToken(user, role, identifier), "Bearer", seconds, user.toDto(),
            false, "Login successful");
    }
}
