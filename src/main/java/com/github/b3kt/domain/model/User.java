package com.github.b3kt.domain.model;

import java.util.Set;

/**
 * Domain entity representing a User.
 * This is the core domain model with business logic.
 */
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Business logic: Check if user can authenticate
     */
    public boolean canAuthenticate() {
        return active && username != null && !username.isEmpty();
    }
}

