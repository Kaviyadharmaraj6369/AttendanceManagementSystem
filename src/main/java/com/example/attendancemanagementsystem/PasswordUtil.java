package com.example.attendancemanagementsystem;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Simple password hashing helper built only with classes already in the JDK
 * (java.security, java.util.Base64) — no extra Maven dependency required.
 *
 * Stored format: "<base64-salt>:<base64-hash>"
 */
public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /** Hash a raw password with a fresh random salt. Use when registering / setting a new password. */
    public static String encode(String rawPassword) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String hash = hash(rawPassword, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash;
    }

    /** Check a raw password against a previously stored "salt:hash" value. Use when logging in. */
    public static boolean matches(String rawPassword, String storedValue) {
        if (rawPassword == null || storedValue == null || !storedValue.contains(":")) {
            return false;
        }
        String[] parts = storedValue.split(":", 2);
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            String expectedHash = parts[1];
            String actualHash = hash(rawPassword, salt);
            return MessageDigest.isEqual(
                    actualHash.getBytes(StandardCharsets.UTF_8),
                    expectedHash.getBytes(StandardCharsets.UTF_8)
            );
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String hash(String rawPassword, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every standard JVM
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}