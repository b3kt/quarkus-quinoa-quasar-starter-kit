package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPelangganEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbPelangganRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbPelangganService extends AbstractCrudService<TbPelangganEntity, Long> {

    @Inject
    TbPelangganRepository repository;

    @Override
    protected PanacheRepositoryBase<TbPelangganEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbPelangganEntity entity, Long id) {
        entity.setId(id);
    }
}

