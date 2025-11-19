package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbSupplierService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSupplierEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import java.util.UUID;

@RequestScoped
@Path("/api/pazaauto/supplier")
public class TbSupplierResource extends AbstractCrudResource<TbSupplierEntity, UUID> {

    @Inject
    TbSupplierService service;

    @Override
    protected AbstractCrudService<TbSupplierEntity, UUID> getService() {
        return service;
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }

    @Override
    protected String getEntityName() {
        return "Supplier";
    }
}

