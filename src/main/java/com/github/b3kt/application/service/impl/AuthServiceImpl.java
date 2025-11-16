package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.LoginResponse;
import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.application.mapper.UserMapper;
import com.github.b3kt.application.service.AuthService;
import com.github.b3kt.domain.exception.AuthenticationException;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.repository.UserRepository;
import com.github.b3kt.infrastructure.security.JwtTokenService;
import com.github.b3kt.infrastructure.security.PasswordEncoder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

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
}

