package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbPenjualanDetailService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPenjualanDetailEntity;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPenjualanDetailId;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Path;

@RequestScoped
@Path("/api/pazaauto/penjualan-detail")
public class TbPenjualanDetailResource extends AbstractCrudResource<TbPenjualanDetailEntity, TbPenjualanDetailId> {

    @Inject
    TbPenjualanDetailService service;

    @Override
    protected AbstractCrudService<TbPenjualanDetailEntity, TbPenjualanDetailId> getService() {
        return service;
    }

    @Override
    protected TbPenjualanDetailId parseId(String id) {
        String[] parts = id.split(":", 2);
        if (parts.length != 2) {
            throw new BadRequestException("Invalid identifier format. Use 'noPenjualan:namaJasaBarang'.");
        }
        return new TbPenjualanDetailId(parts[0], parts[1]);
    }

    @Override
    protected String getEntityName() {
        return "Penjualan Detail";
    }
}

