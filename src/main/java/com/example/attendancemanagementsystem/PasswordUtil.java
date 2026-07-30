package com.example.attendancemanagementsystem;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public final class PasswordUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }


    public static String encode(String rawPassword) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String hash = hash(rawPassword, salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + hash;
    }


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

            throw new RuntimeException("Password hashing failed", e);
        }
    }
}