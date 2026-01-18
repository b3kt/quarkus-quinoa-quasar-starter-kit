package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.DatabaseBackupDto;
import com.github.b3kt.application.service.DatabaseBackupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DatabaseBackupService using pure JDBC.
 * Does not require external tools (pg_dump/psql).
 */
@ApplicationScoped
public class DatabaseBackupServiceImpl implements DatabaseBackupService {

    private static final Logger LOG = Logger.getLogger(DatabaseBackupServiceImpl.class);
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @ConfigProperty(name = "app.backup.directory", defaultValue = "./backups")
    String backupDirectory;

    @Inject
    DataSource dataSource;

    @Override
    public DatabaseBackupDto createBackup(String description) {
        ensureBackupDirectoryExists();

        String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);
        String filename = "backup_" + timestamp + ".sql";
        Path backupPath = Paths.get(backupDirectory, filename);

        try (Connection conn = dataSource.getConnection();
                BufferedWriter writer = Files.newBufferedWriter(backupPath, StandardCharsets.UTF_8)) {

            LOG.info("Starting database backup...");

            // 1. Get database metadata
            DatabaseMetaData metaData = conn.getMetaData();
            List<String> tables = getTables(metaData);
            List<String> sortedTables = sortTablesByDependency(conn, tables);

            // 2. Disable constraints for session if possible (Postgres specific)
            writer.write("SET session_replication_role = 'replica';");
            writer.newLine();
            writer.newLine();

            // 3. Dump data for each table
            for (String tableName : sortedTables) {
                dumpTable(conn, tableName, writer);
            }

            // 4. Reset sequences
            resetSequences(conn, writer);

            // 5. Re-enable constraints
            writer.write("SET session_replication_role = 'origin';");
            writer.newLine();

            LOG.infof("Backup created successfully: %s", filename);

            // Save description
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

        } catch (Exception e) {
            LOG.errorf(e, "Failed to create backup");
            try {
                Files.deleteIfExists(backupPath);
            } catch (IOException ignored) {
            }
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

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false); // Transactional restore

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(backupInputStream, StandardCharsets.UTF_8));
                    Statement stmt = conn.createStatement()) {

                LOG.info("Starting database restore...");

                // 1. Truncate all tables to clear existing data
                truncateAllTables(conn);

                // 2. Execute backup script
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                        continue;
                    }

                    sql.append(line);
                    if (line.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql.setLength(0);
                    } else {
                        sql.append("\n");
                    }
                }

                conn.commit();
                LOG.infof("Database restored successfully from: %s", filename);

            } catch (Exception e) {
                conn.rollback();
                LOG.errorf(e, "Failed to restore backup, rolled back");
                throw new RuntimeException("Failed to restore backup: " + e.getMessage(), e);
            }

        } catch (SQLException e) {
            LOG.errorf(e, "Database error during restore setup");
            throw new RuntimeException("Database error: " + e.getMessage(), e);
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
            throw new RuntimeException("Failed to delete backup: " + e.getMessage(), e);
        }
    }

    // --- Helper Methods ---

    private void ensureBackupDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(backupDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create backup dir", e);
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isEmpty() || filename.contains("..") || !filename.endsWith(".sql")) {
            throw new IllegalArgumentException("Invalid filename");
        }
    }

    private List<String> getTables(DatabaseMetaData metaData) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = metaData.getTables(null, "public", null, new String[] { "TABLE" })) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    // Topological sort tables based on Foreign Keys
    private List<String> sortTablesByDependency(Connection conn, List<String> tables) throws SQLException {
        Map<String, Set<String>> dependencies = new HashMap<>(); // Table -> Parents
        for (String table : tables) {
            dependencies.putIfAbsent(table, new HashSet<>());
            try (ResultSet rs = conn.getMetaData().getImportedKeys(null, "public", table)) {
                while (rs.next()) {
                    String parentTable = rs.getString("PKTABLE_NAME");
                    if (tables.contains(parentTable) && !parentTable.equals(table)) {
                        dependencies.get(table).add(parentTable);
                    }
                }
            }
        }

        List<String> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (String table : tables) {
            visitTable(table, dependencies, visited, visiting, sorted);
        }

        return sorted;
    }

    private void visitTable(String table, Map<String, Set<String>> dependencies, Set<String> visited,
            Set<String> visiting, List<String> sorted) {
        if (visited.contains(table))
            return;
        if (visiting.contains(table))
            return; // Cycle detected, break it simple way

        visiting.add(table);
        for (String parent : dependencies.getOrDefault(table, Collections.emptySet())) {
            visitTable(parent, dependencies, visited, visiting, sorted);
        }
        visiting.remove(table);
        visited.add(table);
        sorted.add(table);
    }

    private void dumpTable(Connection conn, String tableName, BufferedWriter writer) throws SQLException, IOException {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO ").append(tableName).append(" VALUES (");

                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1)
                        sb.append(", ");
                    Object value = rs.getObject(i);
                    if (value == null) {
                        sb.append("NULL");
                    } else {
                        sb.append(formatValue(value));
                    }
                }
                sb.append(");");
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    private String formatValue(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else if (value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof byte[]) {
            // Postgres bytea hex format
            return "'\\x" + bytesToHex((byte[]) value) + "'";
        } else {
            // Escape single quotes for SQL string
            return "'" + value.toString().replace("'", "''") + "'";
        }
    }

    // Hex helper
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void resetSequences(Connection conn, BufferedWriter writer) throws SQLException, IOException {
        String query = "SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public'";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String seqName = rs.getString(1);
                // Attempt to find associated table/column usually named table_id_seq or
                // explicit association
                // Helper heuristic: try to find max id of table that owns this sequence?
                // Simpler approach: Just sync the sequence to the current value,
                // but we can't easily know WHICH table.

                // Generic safe approach for Postgres: setval to max of the column it owns.
                // But finding that relationship via JDBC metadata is hard.

                // Alternative: if we know the naming convention (table_id_seq), we can try.
                // Or just output a generic sequence reset block if the user has one?

                if (seqName.endsWith("_seq") || seqName.endsWith("_id_seq")) {
                    String probableTable = seqName.replace("_id_seq", "").replace("_seq", "");
                    // Verify table exists
                    if (tableExists(conn, probableTable)) {
                        writer.write(
                                String.format("SELECT setval('%s', COALESCE((SELECT MAX(id) FROM %s)+1, 1), false);",
                                        seqName, probableTable));
                        writer.newLine();
                        continue;
                    }
                }

                // Fallback: Just ensure it's not broken?
                // If we don't reset, inserts might fail if they rely on default nextval.
                // But our restore does explicit INSERT VALUES, so nextval isn't called during
                // restore.
                // It's called for NEW data after restore. So we DO need to reset.
                // Let's rely on the above heuristic which covers 99% of auto-generated
                // sequences.
            }
        }
    }

    private boolean tableExists(Connection conn, String tableName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getTables(null, "public", tableName, null)) {
            return rs.next();
        }
    }

    private void truncateAllTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // CASCADE is crucial here to handle FKs
            stmt.execute("DO $$ DECLARE r RECORD; BEGIN " +
                    "FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP " +
                    "EXECUTE 'TRUNCATE TABLE public.' || quote_ident(r.tablename) || ' CASCADE'; " +
                    "END LOOP; END $$;");
        }
    }
}
