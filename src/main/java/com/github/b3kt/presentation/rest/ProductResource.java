package com.github.b3kt.presentation.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import com.github.b3kt.application.dto.ApiResponse;

import java.util.ArrayList;
import java.util.List;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Endpoints to demonstrate RBAC")
public class ProductResource {

    private static final List<String> products = new ArrayList<>();

    static {
        products.add("Laptop");
        products.add("Smartphone");
    }

    @GET
    @RolesAllowed("permission:read")
    public Response getAll() {
        return Response.ok(ApiResponse.success(products)).build();
    }

    @POST
    @RolesAllowed("permission:create")
    public Response create(String name) {
        products.add(name);
        return Response.ok(ApiResponse.success("Product created", name)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("permission:update")
    public Response update(@PathParam("id") int id, String name) {
        if (id >= 0 && id < products.size()) {
            products.set(id, name);
            return Response.ok(ApiResponse.success("Product updated", name)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("permission:delete")
    public Response delete(@PathParam("id") int id) {
        if (id >= 0 && id < products.size()) {
            String removed = products.remove(id);
            return Response.ok(ApiResponse.success("Product deleted", removed)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
