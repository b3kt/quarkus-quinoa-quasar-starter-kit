package com.github.b3kt.plugin.api;

import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * Interface for plugin presentation layer (controller).
 */
public interface PluginController {
    
    /**
     * Map of endpoint paths to their handlers or logic.
     * For a simple implementation, we can have a handle method.
     */
    Response handle(String subPath, String method, Map<String, Object> params, Object body);
    
    /**
     * Get the base path for this controller within the plugin's context.
     */
    default String getPath() {
        return "";
    }
}
