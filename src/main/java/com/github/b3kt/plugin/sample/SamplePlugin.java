package com.github.b3kt.plugin.sample;

import com.github.b3kt.plugin.api.Plugin;
import com.github.b3kt.plugin.api.PluginController;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class SamplePlugin implements Plugin {
    
    private static final Logger LOG = Logger.getLogger(SamplePlugin.class);

    @Inject
    SamplePluginController controller;

    @Override
    public String getId() {
        return "sample";
    }

    @Override
    public String getName() {
        return "Sample Plugin";
    }

    @Override
    public String getDescription() {
        return "A demonstration plugin with its own entities, services, and endpoints.";
    }

    @Override
    public void onEnable() {
        LOG.info("Sample Plugin enabled!");
    }

    @Override
    public void onDisable() {
        LOG.info("Sample Plugin disabled!");
    }

    @Override
    public PluginController getController() {
        return controller;
    }
}
