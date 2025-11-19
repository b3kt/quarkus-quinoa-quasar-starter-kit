package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbJasaEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbJasaRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbJasaService extends AbstractCrudService<TbJasaEntity, Long> {

    @Inject
    TbJasaRepository repository;

    @Override
    protected PanacheRepositoryBase<TbJasaEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbJasaEntity entity, Long id) {
        entity.setId(id);
    }
}

