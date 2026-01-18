package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.DatabaseBackupDto;
import com.github.b3kt.application.service.DatabaseBackupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of DatabaseBackupService using pg_dump and psql commands.
 */
@ApplicationScoped
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    private static final Logger LOG = Logger.getLogger(DatabaseBackupServiceImpl.class);
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Pattern JDBC_URL_PATTERN = Pattern.compile(
            "jdbc:postgresql://([^:/]+)(?::(\\d+))?/([^?]+).*");

    @ConfigProperty(name = "app.backup.directory", defaultValue = "./backups")
    String backupDirectory;

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @ConfigProperty(name = "quarkus.datasource.username")
    String dbUsername;

    @ConfigProperty(name = "quarkus.datasource.password")
    String dbPassword;

    @Inject
    DataSource dataSource;

    @Override
    public DatabaseBackupDto createBackup(String description) {
        ensureBackupDirectoryExists();

        String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);
        String filename = "backup_" + timestamp + ".sql";
        Path backupPath = Paths.get(backupDirectory, filename);

        DatabaseConnectionInfo connInfo = parseJdbcUrl(jdbcUrl);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump",
                    "-h", connInfo.host,
                    "-p", connInfo.port,
                    "-U", dbUsername,
                    "-d", connInfo.database,
                    "-f", backupPath.toString(),
                    "--clean",
                    "--if-exists");
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            String output = readProcessOutput(process);
            boolean completed = process.waitFor(5, TimeUnit.MINUTES);

            if (!completed) {
                process.destroyForcibly();
                throw new RuntimeException("Backup process timed out");
            }

            if (process.exitValue() != 0) {
                throw new RuntimeException("pg_dump failed: " + output);
            }

            LOG.infof("Backup created successfully: %s", filename);

            // Save description to metadata file if provided
            if (description != null && !description.isEmpty()) {
                Path metaPath = Paths.get(backupDirectory, filename + ".meta");
                Files.writeString(metaPath, description);
            }

            File backupFile = backupPath.toFile();
            return new DatabaseBackupDto(
                    filename,
                    backupFile.length(),
                    LocalDateTime.now(),
                    description);
        } catch (IOException | InterruptedException e) {
            LOG.errorf(e, "Failed to create backup");
            throw new RuntimeException("Failed to create backup: " + e.getMessage(), e);
        }
    }

    @Override
    public List<DatabaseBackupDto> listBackups() {
        ensureBackupDirectoryExists();

        List<DatabaseBackupDto> backups = new ArrayList<>();
        File backupDir = new File(backupDirectory);
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".sql"));

        if (files != null) {
            for (File file : files) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    LocalDateTime createdAt = LocalDateTime.ofInstant(
                            attrs.creationTime().toInstant(),
                            ZoneId.systemDefault());

                    // Read description from meta file if exists
                    String description = null;
                    Path metaPath = Paths.get(backupDirectory, file.getName() + ".meta");
                    if (Files.exists(metaPath)) {
                        description = Files.readString(metaPath).trim();
                    }

                    backups.add(new DatabaseBackupDto(
                            file.getName(),
                            file.length(),
                            createdAt,
                            description));
                } catch (IOException e) {
                    LOG.warnf("Failed to read attributes for backup file: %s", file.getName());
                }
            }
        }

        // Sort by creation date descending (newest first)
        backups.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return backups;
    }

    @Override
    public File getBackupFile(String filename) {
        validateFilename(filename);
        File backupFile = Paths.get(backupDirectory, filename).toFile();

        if (!backupFile.exists()) {
            throw new IllegalArgumentException("Backup file not found: " + filename);
        }

        return backupFile;
    }

    @Override
    public void restoreBackup(InputStream backupInputStream, String filename) {
        ensureBackupDirectoryExists();

        // Save uploaded file temporarily
        Path tempBackupPath = Paths.get(backupDirectory, "temp_restore_" + System.currentTimeMillis() + ".sql");

        try {
            // Copy uploaded file to temp location
            Files.copy(backupInputStream, tempBackupPath);

            DatabaseConnectionInfo connInfo = parseJdbcUrl(jdbcUrl);

            // Drop all tables first using SQL
            dropAllTables();

            // Restore using psql
            ProcessBuilder pb = new ProcessBuilder(
                    "psql",
                    "-h", connInfo.host,
                    "-p", connInfo.port,
                    "-U", dbUsername,
                    "-d", connInfo.database,
                    "-f", tempBackupPath.toString());
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            String output = readProcessOutput(process);
            boolean completed = process.waitFor(10, TimeUnit.MINUTES);

            if (!completed) {
                process.destroyForcibly();
                throw new RuntimeException("Restore process timed out");
            }

            if (process.exitValue() != 0) {
                LOG.warnf("psql output (may contain non-fatal errors): %s", output);
            }

            LOG.infof("Database restored successfully from: %s", filename);
        } catch (IOException | InterruptedException e) {
            LOG.errorf(e, "Failed to restore backup");
            throw new RuntimeException("Failed to restore backup: " + e.getMessage(), e);
        } finally {
            // Clean up temp file
            try {
                Files.deleteIfExists(tempBackupPath);
            } catch (IOException e) {
                LOG.warnf("Failed to delete temp restore file: %s", tempBackupPath);
            }
        }
    }

    @Override
    public void deleteBackup(String filename) {
        validateFilename(filename);

        Path backupPath = Paths.get(backupDirectory, filename);
        Path metaPath = Paths.get(backupDirectory, filename + ".meta");

        try {
            if (!Files.exists(backupPath)) {
                throw new IllegalArgumentException("Backup file not found: " + filename);
            }

            Files.delete(backupPath);
            Files.deleteIfExists(metaPath);

            LOG.infof("Backup deleted: %s", filename);
        } catch (IOException e) {
            LOG.errorf(e, "Failed to delete backup: %s", filename);
            throw new RuntimeException("Failed to delete backup: " + e.getMessage(), e);
        }
    }

    /**
     * Drop all tables in the current database to prepare for restore.
     */
    private void dropAllTables() {
        String dropTablesQuery = """
                    DO $$
                    DECLARE
                        r RECORD;
                    BEGIN
                        -- Drop all tables in public schema
                        FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
                            EXECUTE 'DROP TABLE IF EXISTS public.' || quote_ident(r.tablename) || ' CASCADE';
                        END LOOP;

                        -- Drop all sequences in public schema
                        FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public') LOOP
                            EXECUTE 'DROP SEQUENCE IF EXISTS public.' || quote_ident(r.sequence_name) || ' CASCADE';
                        END LOOP;
                    END $$;
                """;

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(dropTablesQuery);
            LOG.info("All tables dropped successfully");
        } catch (Exception e) {
            LOG.errorf(e, "Failed to drop tables");
            throw new RuntimeException("Failed to drop tables: " + e.getMessage(), e);
        }
    }

    private void ensureBackupDirectoryExists() {
        Path backupPath = Paths.get(backupDirectory);
        if (!Files.exists(backupPath)) {
            try {
                Files.createDirectories(backupPath);
                LOG.infof("Created backup directory: %s", backupDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create backup directory: " + backupDirectory, e);
            }
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
        // Prevent directory traversal attacks
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }
        if (!filename.endsWith(".sql")) {
            throw new IllegalArgumentException("Invalid backup file extension");
        }
    }

    private DatabaseConnectionInfo parseJdbcUrl(String url) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid JDBC URL format: " + url);
        }

        String host = matcher.group(1);
        String port = matcher.group(2) != null ? matcher.group(2) : "5432";
        String database = matcher.group(3);

        return new DatabaseConnectionInfo(host, port, database);
    }

    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    private record DatabaseConnectionInfo(String host, String port, String database) {
    }
}
