package com.github.b3kt.application.service;

import com.github.b3kt.application.dto.DatabaseBackupDto;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Service interface for database backup and restore operations.
 */
public interface DatabaseBackupService {

    /**
     * Create a new database backup.
     *
     * @param description Optional description for the backup
     * @return Metadata of the created backup
     */
    DatabaseBackupDto createBackup(String description);

    /**
     * List all available backup files.
     *
     * @return List of backup metadata
     */
    List<DatabaseBackupDto> listBackups();

    /**
     * Get a backup file for download.
     *
     * @param filename The backup filename
     * @return The backup file
     */
    File getBackupFile(String filename);

    /**
     * Restore database from a backup file.
     * This will override all existing data including migration data.
     *
     * @param backupInputStream The backup file input stream
     * @param filename          Original filename for logging
     */
    void restoreBackup(InputStream backupInputStream, String filename);

    /**
     * Delete a backup file.
     *
     * @param filename The backup filename to delete
     */
    void deleteBackup(String filename);
}
