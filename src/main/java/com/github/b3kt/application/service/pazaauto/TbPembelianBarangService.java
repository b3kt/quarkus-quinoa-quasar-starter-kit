package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPembelianBarangEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbPembelianBarangRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TbPembelianBarangService extends AbstractCrudService<TbPembelianBarangEntity, UUID> {

    @Inject
    TbPembelianBarangRepository repository;

    @Override
    protected PanacheRepositoryBase<TbPembelianBarangEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbPembelianBarangEntity entity, UUID id) {
        entity.setId(id);
    }
}

