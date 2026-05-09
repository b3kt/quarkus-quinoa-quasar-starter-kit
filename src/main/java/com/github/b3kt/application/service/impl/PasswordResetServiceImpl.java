package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.service.PasswordResetService;
import com.github.b3kt.domain.exception.InvalidResetTokenException;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.persistence.entity.PasswordResetTokenEntity;
import com.github.b3kt.infrastructure.persistence.repository.PasswordResetTokenRepository;
import com.github.b3kt.infrastructure.repository.UserRepository;
import com.github.b3kt.infrastructure.security.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HexFormat;
import java.util.Optional;

@ApplicationScoped
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordResetTokenRepository tokenRepository;

    @Inject
    PasswordEncoder passwordEncoder;

    @ConfigProperty(name = "app.security.reset-token.expiration-minutes", defaultValue = "15")
    long expirationMinutes;

    @Override
    @Transactional
    public String requestReset(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return null;
        }

        byte[] tokenBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = HexFormat.of().formatHex(tokenBytes);
        String tokenHash = hashToken(rawToken);

        tokenRepository.invalidateAllForUser(username);

        PasswordResetTokenEntity entity = new PasswordResetTokenEntity();
        entity.setTokenHash(tokenHash);
        entity.setUsername(username);
        entity.setExpiresAt(new Date(System.currentTimeMillis() + expirationMinutes * 60_000));
        entity.setUsed(false);
        entity.setCreatedAt(new Date());
        tokenRepository.persist(entity);

        return rawToken;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        String tokenHash = hashToken(token);

        PasswordResetTokenEntity tokenEntity = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidResetTokenException("Invalid or expired reset token"));

        if (tokenEntity.isUsed()) {
            throw new InvalidResetTokenException("Reset token has already been used");
        }

        if (tokenEntity.getExpiresAt().before(new Date())) {
            throw new InvalidResetTokenException("Reset token has expired");
        }

        User user = userRepository.findByUsername(tokenEntity.getUsername())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid or expired reset token"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedPassword);
        userRepository.save(user);

        tokenEntity.setUsed(true);
        tokenRepository.persist(tokenEntity);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash reset token", e);
        }
    }
}
