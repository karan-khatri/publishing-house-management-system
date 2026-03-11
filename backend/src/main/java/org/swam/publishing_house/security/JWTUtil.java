package org.swam.publishing_house.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtil {

    // Read secret key from environment variable with fallback to default (for development only)
    // In production, always set JWT_SECRET environment variable with a strong secret
    private static final String SECRET_KEY = System.getenv("JWT_SECRET") != null 
        ? System.getenv("JWT_SECRET") 
        : "1dd1a4c5a6da46d9176dbb2ca64731607e416268672834320dd3fc9d6ef1f05";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Generate JWT token
     */
    public static String generateToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Validate JWT token
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extract email from token
     */
    public static String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("email", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract role from token
     */
    public static String getRoleFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}