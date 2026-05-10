package com.github.b3kt.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO for login request.
 */
@Schema(description = "Login request with username and password")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    @Schema(description = "Username for authentication", example = "admin", required = true)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "Password for authentication", example = "admin123", required = true)
    private String password;
}

