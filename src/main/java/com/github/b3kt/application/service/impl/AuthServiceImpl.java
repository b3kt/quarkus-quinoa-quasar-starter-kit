package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.LoginResponse;
import com.github.b3kt.application.dto.RegisterRequest;
import com.github.b3kt.application.dto.ChangePasswordRequest;
import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.application.mapper.UserMapper;
import com.github.b3kt.application.service.AuthService;
import com.github.b3kt.domain.exception.AuthenticationException;
import com.github.b3kt.domain.model.Role;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.repository.UserRepository;
import com.github.b3kt.infrastructure.security.JwtTokenService;
import com.github.b3kt.infrastructure.security.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collections;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of authentication service.
 * This orchestrates the authentication use cases.
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    JwtTokenService jwtTokenService;

    @Inject
    PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(String username, String password) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        // Check if user can authenticate
        if (!user.canAuthenticate()) {
            throw new AuthenticationException("User account is not active");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }

        // Generate token
        String token = jwtTokenService.generateToken(user);
        UserInfo userInfo = UserMapper.toUserInfo(user);

        return new LoginResponse(
            token,
            userInfo.getUsername(),
            userInfo.getEmail(),
            jwtTokenService.getTokenExpirationSeconds()
        );
    }

    @Override
    public UserInfo getUserInfo(JsonWebToken jwt) {
        return jwtTokenService.extractUserInfo(jwt);
    }

    @Override
    public UserInfo register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username already exists");
        }

        // Create new user
        User user = new User(
            request.getUsername(),
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),
            Collections.singleton("user")
        );
        String passwordHash = passwordEncoder.encode(password);
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER);

        // Save user
        User savedUser = new User(username, email, passwordHash, roles);

        return UserMapper.toUserInfo(savedUser);
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid old password");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Save user
        userRepository.save(user);
    }
}

