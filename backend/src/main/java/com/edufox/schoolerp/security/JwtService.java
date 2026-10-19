package com.edufox.schoolerp.security;

import com.edufox.schoolerp.auth.AuthUser;
import com.edufox.schoolerp.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final AppProperties properties;

    private SecretKey signingKey() {
        try {
            // Normalize configured material to a fixed 256-bit key so local
            // configuration cannot accidentally start with an insecure HMAC key.
            byte[] key = MessageDigest.getInstance("SHA-256")
                .digest(properties.getAuth().getJwtSecret().getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(key);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    public String issueToken(AuthUser user, String role, String userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(properties.getAuth().getAccessTokenMinutes() * 60);
        return Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .claim("schoolId", user.schoolId())
            .claim("schoolName", user.schoolName())
            .claim("campusId", user.campusId())
            .claim("staffUid", user.staffUid())
            .claim("username", user.username())
            .claim("imagePath", user.imagePath())
            .claim("logoPath", user.logoPath())
            .claim("colorScheme", user.colorScheme())
            .claim("typeStatus", user.typeStatus())
            .claim("dashboard", user.dashboard())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(signingKey())
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }
}
