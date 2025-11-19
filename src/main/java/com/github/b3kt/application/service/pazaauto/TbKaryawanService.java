package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbKaryawanEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbKaryawanRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbKaryawanService extends AbstractCrudService<TbKaryawanEntity, Long> {

    @Inject
    TbKaryawanRepository repository;

    @Override
    protected PanacheRepositoryBase<TbKaryawanEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbKaryawanEntity entity, Long id) {
        entity.setId(id);
    }
}

