package com.github.b3kt.plugin.sample;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class SamplePluginService {
    
    @Inject
    SamplePluginRepository repository;
    
    public List<SamplePluginEntity> getAll() {
        return repository.listAll();
    }
    
    @Transactional
    public SamplePluginEntity create(String name, String description) {
        SamplePluginEntity entity = new SamplePluginEntity();
        entity.setName(name);
        entity.setDescription(description);
        repository.persist(entity);
        return entity;
    }
}
