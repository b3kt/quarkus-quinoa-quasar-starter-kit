package com.github.b3kt.presentation.rest.pazaauto;

import java.time.LocalDateTime;
import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbSpkService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSpkEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/api/pazaauto/spk")
public class TbSpkResource extends AbstractCrudResource<TbSpkEntity, Long> {

    @Inject
    TbSpkService service;

    @Override
    protected AbstractCrudService<TbSpkEntity, Long> getService() {
        return service;
    }

    @Override
    protected Long parseId(String id) {
        return Long.valueOf(id);
    }

    @Override
    protected String getEntityName() {
        return "SPK";
    }

    @GET
    @Path("/get-next-spk-number")
    public Response getNextSpk() {
        String lastSpkNumber = service.getNextSpkNumber(spkNoformatter.format(LocalDateTime.now()));
        String lastQueueNumber = lastSpkNumber.substring(lastSpkNumber.length() - 2);
        int nextQueueNumber = Integer.parseInt(lastQueueNumber) + 1;
        String nextSpkNumber = lastSpkNumber.substring(0, lastSpkNumber.length() - 2)
                + String.format("%02d", nextQueueNumber);
        return Response.ok(ApiResponse.success(nextSpkNumber)).build();
    }

    @Override
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") String id, TbSpkEntity entity) {
        TbSpkEntity updated = getService().update(parseId(id), entity);

        if (updated == null) {
            return Response.ok(ApiResponse.error(getEntityName() + " not found")).build();
        }
        return Response.ok(ApiResponse.success(getEntityName() + " updated", updated)).build();
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        // getService().delete(parseId(id));
        TbSpkEntity entity = getService().findById(parseId(id));
        entity.setKeterangan("SPK Dibatalkan. lastStatus: " + entity.getStatusSpk());
        entity.setStatusSpk("BATAL");
        TbSpkEntity updated = getService().update(parseId(id), entity);

        return Response.ok(ApiResponse.success(getEntityName() + " cancelled", updated)).build();
    }
}
