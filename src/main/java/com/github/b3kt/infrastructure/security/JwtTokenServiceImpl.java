package com.github.b3kt.infrastructure.security;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.application.properties.RbacProperties;
import com.github.b3kt.application.service.RbacService;
import com.github.b3kt.domain.model.Permission;
import com.github.b3kt.domain.model.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of JWT token service using SmallRye JWT.
 */
@ApplicationScoped
public class JwtTokenServiceImpl implements JwtTokenService {

    @ConfigProperty(name = "jwt.issuer", defaultValue = "https://quarkus-quasar.example.com")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    long expirationHours;

    @Inject
    RbacProperties rbacProperties;

    @Inject
    RbacService rbacService;

    @Override
    public String generateToken(User user) {
        io.smallrye.jwt.build.JwtClaimsBuilder jwtBuilder = Jwt.issuer(issuer)
                .upn(user.getUsername())
                .subject(user.getUsername())
                .groups(user.getRoles())
                .claim("email", user.getEmail())
                .expiresIn(Duration.ofHours(expirationHours));

        // If RBAC is enabled, include permissions in the token
        if (rbacProperties.enabled()) {
            try {
                Set<Permission> permissions = rbacService.getUserPermissions(user.getUsername());
                Set<String> permissionNames = permissions.stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                jwtBuilder.claim("permissions", permissionNames);
            } catch (Exception e) {
                // If RBAC is enabled but user permissions can't be fetched, log and continue
                // This allows the token to be generated without permissions
                System.err.println("Warning: Could not fetch permissions for user " + user.getUsername() + ": " + e.getMessage());
            }
        }

        return jwtBuilder.sign();
    }

    @Override
    public UserInfo extractUserInfo(JsonWebToken jwt) {
        String username = jwt.getSubject();
        String email = jwt.getClaim("email");
        java.util.Set<String> roles = jwt.getGroups();

        UserInfo userInfo = new UserInfo(username, email, roles);
        
        // If RBAC is enabled, permissions are already in the token but not in UserInfo
        // UserInfo currently only contains roles, not permissions
        // If you need permissions in UserInfo, you would need to extend UserInfo DTO
        
        return userInfo;
    }

    @Override
    public long getTokenExpirationSeconds() {
        return Duration.ofHours(expirationHours).getSeconds();
    }
}

