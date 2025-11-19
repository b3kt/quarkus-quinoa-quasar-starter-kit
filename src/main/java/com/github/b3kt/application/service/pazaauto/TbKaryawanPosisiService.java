package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbKaryawanPosisiEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbKaryawanPosisiRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TbKaryawanPosisiService extends AbstractCrudService<TbKaryawanPosisiEntity, Long> {

    @Inject
    TbKaryawanPosisiRepository repository;

    @Override
    protected PanacheRepositoryBase<TbKaryawanPosisiEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbKaryawanPosisiEntity entity, Long id) {
        entity.setId(id);
    }
}

