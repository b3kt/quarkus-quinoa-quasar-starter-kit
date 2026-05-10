package com.github.b3kt.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Request to reset password with a reset token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    @Schema(description = "Reset token received from forgot-password request", example = "a1b2c3...", required = true)
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=\\-\\[\\]{}|;:',.<>?/`~]).{8,128}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    @Schema(description = "New password", example = "N3wP@ssword!", required = true, minLength = 8)
    private String newPassword;

}
