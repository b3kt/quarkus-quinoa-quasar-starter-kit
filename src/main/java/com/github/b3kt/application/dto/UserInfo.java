package com.github.b3kt.application.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.Set;

/**
 * DTO for user information.
 */
@Schema(description = "User information")
public class UserInfo {
    @Schema(description = "Username", example = "admin")
    private String username;
    
    @Schema(description = "User email address", example = "admin@example.com")
    private String email;
    
    @Schema(description = "User roles", example = "[\"user\", \"admin\"]")
    private Set<String> roles;

    public UserInfo() {
    }

    public UserInfo(String username, String email, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.roles = roles;
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

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

