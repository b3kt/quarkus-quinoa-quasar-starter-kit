package com.github.b3kt.infrastructure.security;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.application.properties.RbacProperties;
import com.github.b3kt.domain.model.Permission;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of JWT token service using SmallRye JWT.
 */
@ApplicationScoped
public class JwtTokenServiceImpl implements JwtTokenService {

    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "https://quarkus-quasar.example.com")
    String issuer;

    @ConfigProperty(name = "jwt.expiration.hours", defaultValue = "24")
    long expirationHours;

    @Inject
    RbacProperties rbacProperties;

    @Override
    public String generateToken(User user) {
        Set<String> roles = user.getRoles();
        Set<String> permissions = new HashSet<>();

        if (roles != null) {
            for (String role : roles) {
                List<Permission> rolePerms = rbacProperties.roles().get(role);
                if (rolePerms != null) {
                    permissions.addAll(rolePerms.stream()
                            .map(Permission::getAuthority)
                            .collect(Collectors.toSet()));
                }
            }
        }

        // We combine roles and permissions in groups for simplicity with @RolesAllowed
        // or we can use a custom claim for permissions.
        // Quarkus's @RolesAllowed by default checks the "groups" claim.
        Set<String> groups = new HashSet<>(roles != null ? roles : Collections.emptySet());
        groups.addAll(permissions);

        return Jwt.issuer(issuer)
                .upn(user.getUsername())
                .subject(user.getUsername())
                .groups(groups)
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

