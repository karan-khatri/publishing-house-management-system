package org.swam.publishing_house.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for secure password hashing using BCrypt algorithm.
 * BCrypt is specifically designed for password hashing and includes:
 * - Built-in salt generation
 * - Configurable cost factor for computational difficulty
 * - Resistance to rainbow table and brute-force attacks
 */
public class PasswordUtil {

    // Cost factor: 12 provides good security while maintaining acceptable performance
    // Each increment doubles the computational cost
    private static final int BCRYPT_COST = 12;

    /**
     * Hash a password using BCrypt with automatic salt generation
     * @param password The plaintext password to hash
     * @return The BCrypt hash string (includes salt and cost factor)
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verify a password against a BCrypt hash
     * @param password The plaintext password to verify
     * @param storedHash The stored BCrypt hash to compare against
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }
        
        try {
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }
}