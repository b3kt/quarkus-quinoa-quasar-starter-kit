package com.github.b3kt.plugin.manager;

import com.github.b3kt.plugin.api.Plugin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manager for plugin lifecycle and status.
 */
@ApplicationScoped
public class PluginManager {
    
    private static final Logger LOG = Logger.getLogger(PluginManager.class);
    
    @Inject
    Instance<Plugin> pluginInstances;
    
    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();
    private final Map<String, Boolean> pluginStates = new ConcurrentHashMap<>();
    
    public void init() {
        LOG.info("Initializing Plugin Manager...");
        for (Plugin plugin : pluginInstances) {
            String id = plugin.getId();
            plugins.put(id, plugin);
            
            // Check config if enabled, default to true if not specified
            boolean enabled = ConfigProvider.getConfig()
                    .getOptionalValue("app.plugin." + id + ".enabled", Boolean.class)
                    .orElse(true);
            
            pluginStates.put(id, enabled);
            
            if (enabled) {
                LOG.info("Enabling plugin: " + id);
                try {
                    plugin.onEnable();
                } catch (Exception e) {
                    LOG.error("Failed to enable plugin: " + id, e);
                    pluginStates.put(id, false);
                }
            } else {
                LOG.info("Plugin disabled by config: " + id);
            }
        }
    }
    
    public List<Plugin> getEnabledPlugins() {
        return plugins.values().stream()
                .filter(p -> isPluginEnabled(p.getId()))
                .collect(Collectors.toList());
    }
    
    public boolean isPluginEnabled(String pluginId) {
        return pluginStates.getOrDefault(pluginId, false);
    }
    
    public void enablePlugin(String pluginId) {
        Plugin plugin = plugins.get(pluginId);
        if (plugin != null && !isPluginEnabled(pluginId)) {
            plugin.onEnable();
            pluginStates.put(pluginId, true);
            LOG.info("Plugin enabled: " + pluginId);
        }
    }
    
    public void disablePlugin(String pluginId) {
        Plugin plugin = plugins.get(pluginId);
        if (plugin != null && isPluginEnabled(pluginId)) {
            plugin.onDisable();
            pluginStates.put(pluginId, false);
            LOG.info("Plugin disabled: " + pluginId);
        }
    }
    
    public Optional<Plugin> getPlugin(String pluginId) {
        return Optional.ofNullable(plugins.get(pluginId));
    }
    
    public List<Plugin> getAllPlugins() {
        return new ArrayList<>(plugins.values());
    }
}
