package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbPenjualanService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPenjualanEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/api/pazaauto/penjualan")
public class TbPenjualanResource extends AbstractCrudResource<TbPenjualanEntity, String> {

    @Inject
    TbPenjualanService service;

    @Override
    protected AbstractCrudService<TbPenjualanEntity, String> getService() {
        return service;
    }

    @Override
    protected String parseId(String id) {
        return id;
    }

    @Override
    protected String getEntityName() {
        return "Penjualan";
    }
}

