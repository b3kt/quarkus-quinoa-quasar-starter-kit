package com.github.b3kt.presentation.rest.pazaauto;

import java.time.LocalDateTime;
import java.util.List;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.application.service.pazaauto.AbstractCrudService;
import com.github.b3kt.application.service.pazaauto.TbSpkService;
import com.github.b3kt.infrastructure.persistence.entity.pazaauto.TbSpkEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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
}
