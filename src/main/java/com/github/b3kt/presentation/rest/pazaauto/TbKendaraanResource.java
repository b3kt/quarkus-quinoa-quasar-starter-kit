package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbKendaraanService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbKendaraanEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/api/pazaauto/kendaraan")
public class TbKendaraanResource extends AbstractCrudResource<TbKendaraanEntity, Long> {

    @Inject
    TbKendaraanService service;

    @Override
    protected AbstractCrudService<TbKendaraanEntity, Long> getService() {
        return service;
    }

    @Override
    protected Long parseId(String id) {
        return Long.valueOf(id);
    }

    @Override
    protected String getEntityName() {
        return "Kendaraan";
    }
}

