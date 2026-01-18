package com.github.b3kt.application.dto;

/**
 * Request DTO for creating a database backup.
 */
public class DatabaseBackupRequest {

    private String description;

    public DatabaseBackupRequest() {
    }

    public DatabaseBackupRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
