package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSparepartEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbSparepartRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbSparepartService extends AbstractCrudService<TbSparepartEntity, String> {

    @Inject
    TbSparepartRepository repository;

    @Override
    protected PanacheRepositoryBase<TbSparepartEntity, String> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbSparepartEntity entity, String id) {
        entity.setKodeBarang(id);
    }
}

