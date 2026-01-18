package com.github.b3kt.presentation.rest;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.application.dto.DatabaseBackupDto;
import com.github.b3kt.application.dto.DatabaseBackupRequest;
import com.github.b3kt.application.service.DatabaseBackupService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * REST resource for database backup and restore operations.
 */
@RequestScoped
@Path("/api/admin/backup")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "admin" })
public class DatabaseBackupResource {

    private static final Logger LOG = Logger.getLogger(DatabaseBackupResource.class);

    @Inject
    DatabaseBackupService backupService;

    /**
     * Create a new database backup.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBackup(DatabaseBackupRequest request) {
        try {
            String description = request != null ? request.getDescription() : null;
            DatabaseBackupDto backup = backupService.createBackup(description);
            return Response.ok(ApiResponse.success("Backup created successfully", backup)).build();
        } catch (Exception e) {
            LOG.error("Failed to create backup", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to create backup: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * List all available backups.
     */
    @GET
    public Response listBackups() {
        try {
            List<DatabaseBackupDto> backups = backupService.listBackups();
            return Response.ok(ApiResponse.success(backups)).build();
        } catch (Exception e) {
            LOG.error("Failed to list backups", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to list backups: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Download a backup file.
     */
    @GET
    @Path("/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadBackup(@PathParam("filename") String filename) {
        try {
            File backupFile = backupService.getBackupFile(filename);
            return Response.ok(backupFile)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Length", backupFile.length())
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to download backup", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to download backup: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    /**
     * Restore database from an uploaded backup file.
     */
    @POST
    @Path("/restore")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response restoreBackup(MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");

            if (inputParts == null || inputParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("No backup file provided"))
                        .build();
            }

            InputPart inputPart = inputParts.get(0);

            // Extract filename (optional, for logging)
            String filename = "uploaded_backup.sql"; // default
            String[] contentDisposition = inputPart.getHeaders().getFirst("Content-Disposition").split(";");
            for (String split : contentDisposition) {
                if (split.trim().startsWith("filename")) {
                    filename = split.split("=")[1].trim().replaceAll("\"", "");
                }
            }

            try (InputStream inputStream = inputPart.getBody(InputStream.class, null)) {
                backupService.restoreBackup(inputStream, filename);
                return Response.ok(ApiResponse.success("Database restored successfully", null)).build();
            }
        } catch (IOException e) {
            LOG.error("Failed to read uploaded file", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to read uploaded file: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to restore backup", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to restore backup: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete a backup file.
     */
    @DELETE
    @Path("/{filename}")
    public Response deleteBackup(@PathParam("filename") String filename) {
        try {
            backupService.deleteBackup(filename);
            return Response.ok(ApiResponse.success("Backup deleted successfully", null)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        } catch (Exception e) {
            LOG.error("Failed to delete backup", e);
            return Response.serverError()
                    .entity(ApiResponse.error("Failed to delete backup: " + e.getMessage()))
                    .build();
        }
    }
}
