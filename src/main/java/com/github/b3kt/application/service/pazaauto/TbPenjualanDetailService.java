package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPenjualanDetailEntity;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPenjualanDetailId;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbPenjualanDetailRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbPenjualanDetailService extends AbstractCrudService<TbPenjualanDetailEntity, TbPenjualanDetailId> {

    @Inject
    TbPenjualanDetailRepository repository;

    @Override
    protected PanacheRepositoryBase<TbPenjualanDetailEntity, TbPenjualanDetailId> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbPenjualanDetailEntity entity, TbPenjualanDetailId id) {
        entity.setId(id);
    }
}

