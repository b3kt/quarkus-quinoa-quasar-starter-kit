package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbSparepartService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSparepartEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/api/pazaauto/sparepart")
public class TbSparepartResource extends AbstractCrudResource<TbSparepartEntity, String> {

    @Inject
    TbSparepartService service;

    @Override
    protected AbstractCrudService<TbSparepartEntity, String> getService() {
        return service;
    }

    @Override
    protected String parseId(String id) {
        return id;
    }

    @Override
    protected String getEntityName() {
        return "Sparepart";
    }
}

