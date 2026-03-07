package com.github.b3kt.domain.model;

import lombok.Data;

import java.util.Set;

/**
 * Domain entity representing a User.
 * This is the core domain model with business logic.
 */
@Data
public class User {
    private String username;
    private String email;
    private String passwordHash;
    private Set<String> roles;
    private boolean active;

    public User() {
    }

    public User(String username, String email, String passwordHash, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.active = true;
    }

    /**
     * Business logic: Check if user can authenticate
     */
    public boolean canAuthenticate() {
        return active && username != null && !username.isEmpty();
    }
}

