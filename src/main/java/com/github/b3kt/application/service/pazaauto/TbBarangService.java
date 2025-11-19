package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbBarangEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbBarangRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbBarangService extends AbstractCrudService<TbBarangEntity, Long> {

    @Inject
    TbBarangRepository repository;

    @Override
    protected PanacheRepositoryBase<TbBarangEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbBarangEntity entity, Long id) {
        entity.setId(id);
    }
}

