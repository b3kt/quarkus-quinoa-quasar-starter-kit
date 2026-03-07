package com.github.b3kt.presentation.rest.plugin;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.plugin.api.Plugin;
import com.github.b3kt.plugin.api.PluginController;
import com.github.b3kt.plugin.manager.PluginManager;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Common presentation layer for plugin controllers.
 */
@Slf4j
@Path("/api/ext")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Plugins", description = "Endpoints for plugin management and plugin-provided functionality")
public class PluginResource {

    @Inject
    PluginManager pluginManager;

    @GET
    @PermitAll
    @Operation(summary = "List all plugins and their status")
    public Response listPlugins() {
        return Response.ok(ApiResponse.success(
            pluginManager.getAllPlugins().stream()
                .map(p -> Map.of(
                    "id", p.getId(),
                    "name", p.getName(),
                    "description", p.getDescription(),
                    "enabled", pluginManager.isPluginEnabled(p.getId())
                ))
                .toList()
        )).build();
    }

    @POST
    @Path("/{pluginId}/enable")
    @PermitAll // Should be Admin in production
    @Operation(summary = "Enable a plugin")
    public Response enablePlugin(@PathParam("pluginId") String pluginId) {
        if (pluginManager.getPlugin(pluginId).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Plugin not found"))
                    .build();
        }
        pluginManager.enablePlugin(pluginId);
        return Response.ok(ApiResponse.success("Plugin enabled", null)).build();
    }

    @POST
    @Path("/{pluginId}/disable")
    @PermitAll // Should be Admin in production
    @Operation(summary = "Disable a plugin")
    public Response disablePlugin(@PathParam("pluginId") String pluginId) {
        if (pluginManager.getPlugin(pluginId).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Plugin not found"))
                    .build();
        }
        pluginManager.disablePlugin(pluginId);
        return Response.ok(ApiResponse.success("Plugin disabled", null)).build();
    }

    @GET
    @Path("/{pluginId}/{subPath:.*}")
    @PermitAll
    @Operation(summary = "Generic GET endpoint for plugins")
    public Response handleGet(
            @PathParam("pluginId") String pluginId,
            @PathParam("subPath") String subPath,
            @Context UriInfo uriInfo) {
        return dispatch(pluginId, subPath, "GET", uriInfo, null);
    }

    @POST
    @Path("/{pluginId}/{subPath:.*}")
    @PermitAll
    @Operation(summary = "Generic POST endpoint for plugins")
    public Response handlePost(
            @PathParam("pluginId") String pluginId,
            @PathParam("subPath") String subPath,
            @Context UriInfo uriInfo,
            Map<String, Object> body) {
        return dispatch(pluginId, subPath, "POST", uriInfo, body);
    }
    
    @PUT
    @Path("/{pluginId}/{subPath:.*}")
    @PermitAll
    @Operation(summary = "Generic PUT endpoint for plugins")
    public Response handlePut(
            @PathParam("pluginId") String pluginId,
            @PathParam("subPath") String subPath,
            @Context UriInfo uriInfo,
            Map<String, Object> body) {
        return dispatch(pluginId, subPath, "PUT", uriInfo, body);
    }

    @DELETE
    @Path("/{pluginId}/{subPath:.*}")
    @PermitAll
    @Operation(summary = "Generic DELETE endpoint for plugins")
    public Response handleDelete(
            @PathParam("pluginId") String pluginId,
            @PathParam("subPath") String subPath,
            @Context UriInfo uriInfo) {
        return dispatch(pluginId, subPath, "DELETE", uriInfo, null);
    }

    private Response dispatch(String pluginId, String subPath, String method, UriInfo uriInfo, Object body) {
        Optional<Plugin> pluginOpt = pluginManager.getPlugin(pluginId);
        
        if (pluginOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error("Plugin not found: " + pluginId))
                    .build();
        }

        if (!pluginManager.isPluginEnabled(pluginId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(ApiResponse.error("Plugin is disabled: " + pluginId))
                    .build();
        }

        Plugin plugin = pluginOpt.get();
        PluginController controller = plugin.getController();

        if (controller == null) {
            return Response.status(Response.Status.NOT_IMPLEMENTED)
                    .entity(ApiResponse.error("Plugin does not provide a controller: " + pluginId))
                    .build();
        }

        Map<String, Object> params = new HashMap<>();
        uriInfo.getQueryParameters().forEach((key, value) -> {
            if (value.size() == 1) {
                params.put(key, value.get(0));
            } else {
                params.put(key, value);
            }
        });

        try {
            return controller.handle(subPath, method, params, body);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error in plugin " + pluginId + ": " + e.getMessage()))
                    .build();
        }
    }
}
