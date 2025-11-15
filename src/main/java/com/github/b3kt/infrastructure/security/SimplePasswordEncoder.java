package com.github.b3kt.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Simple password encoder implementation.
 * NOTE: This is a demo implementation. In production, use BCrypt or Argon2.
 */
@ApplicationScoped
public class SimplePasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        // In production, use proper hashing like BCrypt
        // For demo purposes, we're storing plain text (NOT SECURE!)
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        // In production, use proper password verification
        // For demo purposes, simple string comparison
        return rawPassword != null && rawPassword.equals(encodedPassword);
    }
}

