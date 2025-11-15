# PostgreSQL Setup Guide

This project now uses PostgreSQL as the database. Follow these steps to set it up.

## Prerequisites

1. **Install PostgreSQL**
   - Ubuntu/Debian: `sudo apt-get install postgresql postgresql-contrib`
   - macOS: `brew install postgresql`
   - Windows: Download from https://www.postgresql.org/download/

2. **Start PostgreSQL Service**
   ```bash
   # Linux
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   
   # macOS
   brew services start postgresql
   ```

## Database Setup

1. **Connect to PostgreSQL**
   ```bash
   sudo -u postgres psql
   # Or on macOS:
   psql postgres
   ```

2. **Create Database and User**
   ```sql
   CREATE DATABASE quarkusdb;
   CREATE USER quarkus WITH PASSWORD 'quarkus';
   GRANT ALL PRIVILEGES ON DATABASE quarkusdb TO quarkus;
   \q
   ```

3. **Verify Connection**
   ```bash
   psql -U quarkus -d quarkusdb -h localhost
   ```

## Configuration

The application is configured to use PostgreSQL with these defaults:
- **Host:** localhost
- **Port:** 5432
- **Database:** quarkusdb
- **Username:** quarkus
- **Password:** quarkus

### Using Environment Variables

You can override the defaults using environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/quarkusdb
export DB_USERNAME=quarkus
export DB_PASSWORD=quarkus
./mvnw quarkus:dev
```

### Custom Configuration

Edit `application.properties` to change the connection details:

```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://your-host:5432/your-database
quarkus.datasource.username=your-username
quarkus.datasource.password=your-password
```

## Running the Application

1. **Start PostgreSQL** (if not already running)

2. **Run the application:**
   ```bash
   ./mvnw quarkus:dev
   ```

3. **Verify migrations:**
   - The application will automatically run Flyway migrations on startup
   - Check the logs to confirm migrations ran successfully

## Troubleshooting

### Connection Refused

**Error:** `Connection to localhost:5432 refused`

**Solution:**
1. Check if PostgreSQL is running:
   ```bash
   sudo systemctl status postgresql
   ```
2. Verify PostgreSQL is listening on port 5432:
   ```bash
   sudo netstat -tlnp | grep 5432
   ```

### Authentication Failed

**Error:** `FATAL: password authentication failed`

**Solution:**
1. Verify username and password in `application.properties`
2. Check PostgreSQL user exists:
   ```sql
   SELECT * FROM pg_user WHERE usename = 'quarkus';
   ```
3. Reset password if needed:
   ```sql
   ALTER USER quarkus WITH PASSWORD 'quarkus';
   ```

### Database Does Not Exist

**Error:** `FATAL: database "quarkusdb" does not exist`

**Solution:**
1. Create the database:
   ```sql
   CREATE DATABASE quarkusdb;
   ```

### Migration Errors

**Error:** Flyway migration failures

**Solution:**
1. Check PostgreSQL logs for detailed error messages
2. Verify database user has proper permissions
3. Ensure tables don't already exist (if recreating):
   ```sql
   DROP TABLE IF EXISTS user_roles CASCADE;
   DROP TABLE IF EXISTS users CASCADE;
   ```

## Production Recommendations

1. **Use Strong Passwords:** Change default password in production
2. **Connection Pooling:** Quarkus handles this automatically
3. **Backup Strategy:** Set up regular PostgreSQL backups
4. **SSL/TLS:** Enable SSL connections in production
5. **Environment Variables:** Use environment variables for sensitive data
6. **Monitoring:** Set up PostgreSQL monitoring and logging

## Useful Commands

```bash
# Connect to database
psql -U quarkus -d quarkusdb -h localhost

# List all databases
psql -U postgres -c "\l"

# List all tables
psql -U quarkus -d quarkusdb -c "\dt"

# View users table
psql -U quarkus -d quarkusdb -c "SELECT * FROM users;"

# View user roles
psql -U quarkus -d quarkusdb -c "SELECT * FROM user_roles;"
```

