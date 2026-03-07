package com.github.b3kt.plugin.manager;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class PluginInitializer {
    
    @Inject
    PluginManager pluginManager;
    
    void onStart(@Observes StartupEvent ev) {
        pluginManager.init();
    }
}
