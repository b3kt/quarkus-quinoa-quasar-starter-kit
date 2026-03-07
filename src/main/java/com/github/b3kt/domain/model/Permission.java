package com.github.b3kt.domain.model;

/**
 * Enum representing granular permissions for operations.
 */
public enum Permission {
    READ,
    CREATE,
    UPDATE,
    DELETE;
    
    public String getAuthority() {
        return "permission:" + name().toLowerCase();
    }

    public String getAuthority(String role, String entity) {
        return role + ":" + entity + ":" + name().toLowerCase();
    }
}
