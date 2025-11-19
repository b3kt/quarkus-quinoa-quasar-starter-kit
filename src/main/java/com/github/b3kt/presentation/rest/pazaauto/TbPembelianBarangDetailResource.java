package com.github.b3kt.presentation.rest.pazaauto;

import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbPembelianBarangDetailService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbPembelianBarangDetailEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;

import java.util.UUID;

@RequestScoped
@Path("/api/pazaauto/pembelian-barang-detail")
public class TbPembelianBarangDetailResource extends AbstractCrudResource<TbPembelianBarangDetailEntity, UUID> {

    @Inject
    TbPembelianBarangDetailService service;

    @Override
    protected AbstractCrudService<TbPembelianBarangDetailEntity, UUID> getService() {
        return service;
    }

    @Override
    protected UUID parseId(String id) {
        return UUID.fromString(id);
    }

    @Override
    protected String getEntityName() {
        return "Pembelian Barang Detail";
    }
}

