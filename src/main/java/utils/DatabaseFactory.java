package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DatabaseFactory - Thread-safe database connection management
 * Supports parallel test execution with ThreadLocal connections
 */
public class DatabaseFactory {

    // ThreadLocal for parallel execution
    private static ThreadLocal<Connection> connection = new ThreadLocal<>();

    // Connection pools for different database types
    private static final ConcurrentHashMap<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    // Database connection initialization
    public static Connection initDatabaseConnection() {
        if (connection.get() == null || isConnectionClosed(connection.get())) {
            try {
                String dbType = ConfigReader.getProperty("db.type");
                HikariDataSource dataSource = getOrCreateDataSource(dbType);

                Connection conn = dataSource.getConnection();
                connection.set(conn);

                System.out.println("✅ Database connection established for thread: " + Thread.currentThread().getName());
                System.out.println("📊 Database type: " + dbType.toUpperCase());

            } catch (SQLException e) {
                System.err.println("❌ Failed to establish database connection: " + e.getMessage());
                throw new RuntimeException("Database connection failed", e);
            }
        }
        return connection.get();
    }

    // Get or create HikariCP data source
    private static HikariDataSource getOrCreateDataSource(String dbType) {
        return dataSources.computeIfAbsent(dbType, type -> {
            HikariConfig config = new HikariConfig();

            // Basic connection properties
            config.setJdbcUrl(ConfigReader.getProperty("db.url"));
            config.setUsername(ConfigReader.getProperty("db.username"));
            config.setPassword(ConfigReader.getProperty("db.password"));
            config.setDriverClassName(ConfigReader.getProperty("db.driver"));

            // Connection pool settings
            String maxPoolSize = ConfigReader.getProperty("db.pool.maxSize");
            config.setMaximumPoolSize(Integer.parseInt(maxPoolSize != null ? maxPoolSize : "10"));

            String minIdle = ConfigReader.getProperty("db.pool.minIdle");
            config.setMinimumIdle(Integer.parseInt(minIdle != null ? minIdle : "2"));

            String timeout = ConfigReader.getProperty("db.timeout");
            config.setConnectionTimeout(Long.parseLong(timeout != null ? timeout : "30000"));

            // Connection validation
            config.setConnectionTestQuery("SELECT 1");
            config.setValidationTimeout(5000);

            // Pool naming
            config.setPoolName("AutomationTestPool-" + type);

            // Additional optimizations for test environment
            config.setLeakDetectionThreshold(60000); // 1 minute
            config.setIdleTimeout(300000); // 5 minutes
            config.setMaxLifetime(600000); // 10 minutes

            System.out.println("🏗️ Created new connection pool for database type: " + type.toUpperCase());

            return new HikariDataSource(config);
        });
    }

    // Get current thread's database connection
    public static Connection getDatabaseConnection() {
        Connection conn = connection.get();
        if (conn == null || isConnectionClosed(conn)) {
            return initDatabaseConnection();
        }
        return conn;
    }

    // Check if connection is closed
    private static boolean isConnectionClosed(Connection conn) {
        try {
            return conn == null || conn.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    // Test database connection
    public static boolean testConnection() {
        try {
            Connection conn = getDatabaseConnection();
            return !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Get database type
    public static String getDatabaseType() {
        String dbType = ConfigReader.getProperty("db.type");
        return dbType != null ? dbType : "unknown";
    }

    // Get database URL
    public static String getDatabaseUrl() {
        return ConfigReader.getProperty("db.url");
    }

    // Close current thread's connection
    public static void closeDatabaseConnection() {
        Connection conn = connection.get();
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("✅ Database connection closed for thread: " + Thread.currentThread().getName());
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Error closing database connection: " + e.getMessage());
            } finally {
                connection.remove();
            }
        }
    }

    // Clean up ThreadLocal to prevent memory leaks
    public static void cleanupThreadLocal() {
        closeDatabaseConnection();
        connection.remove();
    }

    // Shutdown all connection pools (use in application shutdown)
    public static void shutdownAllPools() {
        dataSources.values().forEach(dataSource -> {
            if (!dataSource.isClosed()) {
                dataSource.close();
                System.out.println("🔒 Connection pool shut down");
            }
        });
        dataSources.clear();
    }

    // Get connection pool statistics
    public static void printPoolStatistics() {
        dataSources.forEach((type, dataSource) -> {
            System.out.println("📊 Pool Statistics for " + type.toUpperCase() + ":");
            System.out.println("   - Active Connections: " + dataSource.getHikariPoolMXBean().getActiveConnections());
            System.out.println("   - Idle Connections: " + dataSource.getHikariPoolMXBean().getIdleConnections());
            System.out.println("   - Total Connections: " + dataSource.getHikariPoolMXBean().getTotalConnections());
            System.out.println(
                    "   - Threads Awaiting: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        });
    }
}