package com.github.b3kt.application.dto;

import java.time.LocalDateTime;

/**
 * DTO for database backup metadata.
 */
public class DatabaseBackupDto {

    private String filename;
    private Long sizeBytes;
    private LocalDateTime createdAt;
    private String description;

    public DatabaseBackupDto() {
    }

    public DatabaseBackupDto(String filename, Long sizeBytes, LocalDateTime createdAt, String description) {
        this.filename = filename;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.description = description;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
