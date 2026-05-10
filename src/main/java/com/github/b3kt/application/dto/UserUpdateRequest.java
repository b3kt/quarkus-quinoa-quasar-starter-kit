package com.github.b3kt.application.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Request to update user roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotEmpty(message = "At least one role is required")
    @Schema(description = "Set of roles to assign", example = "[\"user\", \"admin\"]", required = true)
    private Set<String> roles;
}
