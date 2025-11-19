package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbKendaraanEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbKendaraanRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbKendaraanService extends AbstractCrudService<TbKendaraanEntity, Long> {

    @Inject
    TbKendaraanRepository repository;

    @Override
    protected PanacheRepositoryBase<TbKendaraanEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbKendaraanEntity entity, Long id) {
        entity.setId(id);
    }
}

