package com.github.b3kt.infrastructure.persistence.repository;

import com.github.b3kt.infrastructure.persistence.entity.PasswordResetTokenEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PasswordResetTokenRepository implements PanacheRepository<PasswordResetTokenEntity> {

    public Optional<PasswordResetTokenEntity> findByTokenHash(String tokenHash) {
        return find("tokenHash", tokenHash).firstResultOptional();
    }

    public void invalidateAllForUser(String username) {
        update("used = true where username = ?1 and used = false", username);
    }
}
