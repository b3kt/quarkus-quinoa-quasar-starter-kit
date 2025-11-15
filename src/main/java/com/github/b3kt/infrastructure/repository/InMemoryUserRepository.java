package com.github.b3kt.infrastructure.repository;

import com.github.b3kt.domain.model.User;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository.
 * This is a demo implementation. In production, replace with database implementation.
 */
@ApplicationScoped
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        // Initialize with demo user
        Set<String> roles = new HashSet<>();
        roles.add("user");
        roles.add("admin");
        
        User demoUser = new User(
            "admin",
            "admin@example.com",
            "admin123", // In production, this should be hashed
            roles
        );
        users.put("admin", demoUser);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public User save(User user) {
        users.put(user.getUsername(), user);
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return users.containsKey(username);
    }
}

