package com.github.b3kt.plugin.sample;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.plugin.api.PluginController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@ApplicationScoped
public class SamplePluginController implements PluginController {

    @Inject
    SamplePluginService service;

    @Override
    public Response handle(String subPath, String method, Map<String, Object> params, Object body) {
        if ("GET".equalsIgnoreCase(method)) {
            if ("data".equals(subPath)) {
                return Response.ok(ApiResponse.success(service.getAll())).build();
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            if ("data".equals(subPath) && body instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) body;
                String name = (String) data.get("name");
                String description = (String) data.get("description");
                return Response.ok(ApiResponse.success("Created", service.create(name, description))).build();
            }
        }
        
        return Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse.error("Unknown endpoint in Sample Plugin: " + method + " " + subPath))
                .build();
    }
}
