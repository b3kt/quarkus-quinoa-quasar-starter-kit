package com.github.b3kt.application.dto;

import jakarta.validation.constraints.NotEmpty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Request to update user roles")
public class UserUpdateRequest {

    @NotEmpty(message = "At least one role is required")
    @Schema(description = "Set of roles to assign", example = "[\"user\", \"admin\"]", required = true)
    private Set<String> roles;

    public UserUpdateRequest() {}

    public UserUpdateRequest(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
