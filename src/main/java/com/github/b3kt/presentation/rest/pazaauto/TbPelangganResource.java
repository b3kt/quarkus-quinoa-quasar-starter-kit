package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbPelangganService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPelangganEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/api/pazaauto/pelanggan")
public class TbPelangganResource extends AbstractCrudResource<TbPelangganEntity, Long> {

    @Inject
    TbPelangganService service;

    @Override
    protected AbstractCrudService<TbPelangganEntity, Long> getService() {
        return service;
    }

    @Override
    protected Long parseId(String id) {
        return Long.valueOf(id);
    }

    @Override
    protected String getEntityName() {
        return "Pelanggan";
    }
}

