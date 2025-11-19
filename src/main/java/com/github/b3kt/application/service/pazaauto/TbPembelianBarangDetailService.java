package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPembelianBarangDetailEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbPembelianBarangDetailRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TbPembelianBarangDetailService extends AbstractCrudService<TbPembelianBarangDetailEntity, UUID> {

    @Inject
    TbPembelianBarangDetailRepository repository;

    @Override
    protected PanacheRepositoryBase<TbPembelianBarangDetailEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbPembelianBarangDetailEntity entity, UUID id) {
        entity.setId(id);
    }
}

