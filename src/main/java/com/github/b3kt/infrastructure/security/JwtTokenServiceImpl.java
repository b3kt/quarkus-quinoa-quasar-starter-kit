package com.github.b3kt.infrastructure.security;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.domain.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;

/**
 * Implementation of JWT token service using SmallRye JWT.
 */
@ApplicationScoped
public class JwtTokenServiceImpl implements JwtTokenService {

    @ConfigProperty(name = "jwt.issuer", defaultValue = "https://quarkus-quasar.example.com")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    long expirationHours;

    @Override
    public String generateToken(User user) {
        return Jwt.issuer(issuer)
                .upn(user.getUsername())
                .subject(user.getUsername())
                .groups(user.getRoles())
                .claim("email", user.getEmail())
                .expiresIn(Duration.ofHours(expirationHours))
                .sign();
    }

    @Override
    public UserInfo extractUserInfo(JsonWebToken jwt) {
        String username = jwt.getSubject();
        String email = jwt.getClaim("email");
        java.util.Set<String> roles = jwt.getGroups();

        return new UserInfo(username, email, roles);
    }

    @Override
    public long getTokenExpirationSeconds() {
        return Duration.ofHours(expirationHours).getSeconds();
    }
}

