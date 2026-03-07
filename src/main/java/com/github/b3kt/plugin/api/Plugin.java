package com.github.b3kt.plugin.api;

import java.util.Map;

/**
 * Base interface for application-level plugins.
 */
public interface Plugin {
    
    /**
     * Get unique identifier for the plugin.
     */
    String getId();
    
    /**
     * Get display name of the plugin.
     */
    String getName();
    
    /**
     * Get description of the plugin.
     */
    String getDescription();
    
    /**
     * Lifecycle method: Called when plugin is enabled.
     */
    default void onEnable() {}
    
    /**
     * Lifecycle method: Called when plugin is disabled.
     */
    default void onDisable() {}
    
    /**
     * Plugin metadata/configuration.
     */
    default Map<String, Object> getMetadata() {
        return Map.of();
    }
    
    /**
     * Get the controller for this plugin if it has one.
     */
    default PluginController getController() {
        return null;
    }
}
