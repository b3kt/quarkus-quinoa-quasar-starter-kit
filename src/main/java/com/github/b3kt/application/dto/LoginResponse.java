package com.github.b3kt.application.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for login response containing JWT token and user info.
 */
@Schema(description = "Login response containing JWT token and user information")
public class LoginResponse {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Username", example = "admin")
    private String username;
    
    @Schema(description = "User email address", example = "admin@example.com")
    private String email;
    
    @Schema(description = "Token expiration time in seconds", example = "86400")
    private Long expiresIn;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String email, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

