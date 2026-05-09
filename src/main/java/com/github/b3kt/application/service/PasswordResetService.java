package com.github.b3kt.application.service;

public interface PasswordResetService {

    /**
     * Initiate a password reset for the given username.
     * Returns the raw reset token if the user exists, or null if not found
     * (same response to prevent user enumeration).
     */
    String requestReset(String username);

    /**
     * Reset the password using a valid reset token.
     *
     * @throws com.github.b3kt.domain.exception.InvalidResetTokenException if token is invalid, expired, or already used
     */
    void resetPassword(String token, String newPassword);
}
