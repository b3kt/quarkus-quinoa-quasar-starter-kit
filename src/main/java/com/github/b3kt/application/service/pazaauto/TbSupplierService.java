package com.github.b3kt.application.service.pazaauto;

import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSupplierEntity;
import com.github.b3kt.infrastructure.persistence.repository.pazaauto.TbSupplierRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class TbSupplierService extends AbstractCrudService<TbSupplierEntity, UUID> {

    @Inject
    TbSupplierRepository repository;

    @Override
    protected PanacheRepositoryBase<TbSupplierEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    protected void setEntityId(TbSupplierEntity entity, UUID id) {
        entity.setId(id);
    }
}

