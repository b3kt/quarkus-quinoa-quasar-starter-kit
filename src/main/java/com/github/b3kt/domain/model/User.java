package com.github.b3kt.domain.model;

import java.util.Set;

import com.github.b3kt.infrastructure.persistence.entity.RoleEntity;

/**
 * Domain entity representing a User.
 * This is the core domain model with business logic.
 */
public class User {
    private String username;
    private String email;
    private String passwordHash;
    private Set<RoleEntity> roles;
    private boolean active;

    public User() {
    }

    public User(String username, String email, String passwordHash, Set<RoleEntity> roles) {
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

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
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
