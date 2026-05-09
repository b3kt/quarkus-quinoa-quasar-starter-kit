package com.github.b3kt.presentation.rest;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.application.dto.UserUpdateRequest;
import com.github.b3kt.application.service.UserService;
import com.github.b3kt.domain.exception.UserNotFoundException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin", description = "Admin-only user management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminResource {

    @Inject
    UserService userService;

    @GET
    @Path("/users")
    @Operation(
        summary = "List all users",
        description = "Retrieve a list of all registered users (admin only)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "List of users retrieved successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public Response listUsers() {
        List<UserInfo> users = userService.findAll();
        return Response.ok(ApiResponse.success(users)).build();
    }

    @GET
    @Path("/users/{username}")
    @Operation(
        summary = "Get user by username",
        description = "Retrieve details of a specific user (admin only)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "User not found"),
        @APIResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public Response getUser(@PathParam("username") String username) {
        try {
            UserInfo userInfo = userService.findByUsername(username);
            return Response.ok(ApiResponse.success(userInfo)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/users/{username}/roles")
    @Operation(
        summary = "Update user roles",
        description = "Update the roles assigned to a user (admin only)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "Roles updated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "User not found"),
        @APIResponse(responseCode = "400", description = "Invalid role data"),
        @APIResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public Response updateRoles(@PathParam("username") String username, @Valid UserUpdateRequest request) {
        try {
            UserInfo userInfo = userService.updateRoles(username, request.getRoles());
            return Response.ok(ApiResponse.success("Roles updated successfully", userInfo)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/users/{username}/activate")
    @Operation(
        summary = "Activate user",
        description = "Reactivate a deactivated user account (admin only)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User activated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "User not found"),
        @APIResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public Response activateUser(@PathParam("username") String username) {
        try {
            UserInfo userInfo = userService.activateUser(username, true);
            return Response.ok(ApiResponse.success("User activated successfully", userInfo)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/users/{username}/deactivate")
    @Operation(
        summary = "Deactivate user",
        description = "Deactivate a user account, preventing login (admin only)"
    )
    @APIResponses({
        @APIResponse(
            responseCode = "200",
            description = "User deactivated successfully",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = ApiResponse.class))
        ),
        @APIResponse(responseCode = "404", description = "User not found"),
        @APIResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
    public Response deactivateUser(@PathParam("username") String username) {
        try {
            UserInfo userInfo = userService.activateUser(username, false);
            return Response.ok(ApiResponse.success("User deactivated successfully", userInfo)).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }
}
