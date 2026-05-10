package com.github.b3kt.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for login response containing JWT token and user info.
 */
@Schema(description = "Login response containing JWT token and user information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Username", example = "admin")
    private String username;
    
    @Schema(description = "User email address", example = "admin@example.com")
    private String email;
    
    @Schema(description = "Token expiration time in seconds", example = "86400")
    private Long expiresIn;

}

