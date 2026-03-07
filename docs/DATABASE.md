# Database Configuration Guide

This project uses PostgreSQL database.

## PostgreSQL Database

PostgreSQL is a production-ready, open-source relational database.

**Prerequisites:**
1. Install PostgreSQL
2. Create database and user (see POSTGRESQL_SETUP.md for details)

**Usage:**
```bash
./mvnw quarkus:dev
```

**Default Configuration:**
- Database: PostgreSQL
- URL: `jdbc:postgresql://localhost:5432/quarkusdb`
- Username: `quarkus` (configurable via `DB_USERNAME`)
- Password: `quarkus` (configurable via `DB_PASSWORD`)

**Using Environment Variables:**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/quarkusdb
export DB_USERNAME=quarkus
export DB_PASSWORD=quarkus
./mvnw quarkus:dev
```

## Repository Types

The application supports two repository implementations:

### 1. JPA Repository (Default)
Uses database persistence via Hibernate/JPA.

**Configuration:**
```properties
app.repository.type=jpa
```

### 2. In-Memory Repository
Uses in-memory storage (no database required).

**Configuration:**
```properties
app.repository.type=memory
```

**Usage:**
```bash
./mvnw quarkus:dev -Dapp.repository.type=memory
```

## Database Migrations

The project uses Flyway for database migrations. Migrations are located in:
```
src/main/resources/db/migration/
```

**Migration Files:**
- `V1__create_users_table.sql` - Creates users and user_roles tables
- `V2__insert_demo_user.sql` - Inserts demo user (admin/admin123)

Migrations run automatically on application startup.

## Demo User

Default credentials:
- **Username:** `admin`
- **Password:** `admin123`

This user is created automatically via Flyway migration.

## Setup Instructions

For detailed PostgreSQL setup instructions, see [POSTGRESQL_SETUP.md](POSTGRESQL_SETUP.md).

Quick setup:
```sql
CREATE DATABASE quarkusdb;
CREATE USER quarkus WITH PASSWORD 'quarkus';
GRANT ALL PRIVILEGES ON DATABASE quarkusdb TO quarkus;
```

## Troubleshooting

### Connection Issues

1. **Check PostgreSQL is running:**
   ```bash
   sudo systemctl status postgresql
   ```

2. **Verify database exists:**
   ```sql
   \l
   ```

3. **Check connection:**
   ```bash
   psql -U quarkus -d quarkusdb -h localhost
   ```

### Migration Issues

If migrations fail:
1. Check Flyway logs in application output
2. Verify SQL syntax is compatible with PostgreSQL
3. Check application logs for errors
4. Ensure database user has proper permissions

### Repository Not Working

1. Verify `app.repository.type` is set correctly
2. Check that database is accessible
3. Verify JPA entities are properly configured
4. Check application logs for errors

## Production Recommendations

1. **Use strong passwords** - Change default password
2. **Enable SSL/TLS** - Secure database connections
3. **Use connection pooling** - Configured automatically by Quarkus
4. **Set up database backups** - Regular PostgreSQL backups
5. **Use environment variables** - For sensitive configuration
6. **Enable password hashing** - Replace SimplePasswordEncoder with BCrypt
7. **Monitor database** - Set up PostgreSQL monitoring
