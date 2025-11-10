package utils;

import io.qameta.allure.Step;
import java.sql.*;
import java.util.*;

/**
 * DatabaseUtils - Common database operations and utilities
 * Supports CRUD operations, data validation, and test data management
 */
public class DatabaseUtils {

    /**
     * Execute a SELECT query and return results as List of Maps
     */
    @Step("Execute SELECT query: {query}")
    public static List<Map<String, Object>> executeSelectQuery(String query, Object... parameters) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = DatabaseFactory.getDatabaseConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters if provided
            setParameters(stmt, parameters);

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }

            System.out.println("✅ SELECT query executed successfully. Rows returned: " + results.size());
            AllureManager.addStep("Query executed successfully. Rows returned: " + results.size());

        } catch (SQLException e) {
            String errorMsg = "❌ Failed to execute SELECT query: " + e.getMessage();
            System.err.println(errorMsg);
            AllureManager.addStep("Query execution failed: " + e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }

        return results;
    }

    /**
     * Execute INSERT, UPDATE, or DELETE query
     */
    @Step("Execute {operationType} query: {query}")
    public static int executeUpdateQuery(String query, String operationType, Object... parameters) {
        int rowsAffected = 0;

        try (Connection conn = DatabaseFactory.getDatabaseConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set parameters if provided
            setParameters(stmt, parameters);

            rowsAffected = stmt.executeUpdate();

            System.out.println("✅ " + operationType + " query executed successfully. Rows affected: " + rowsAffected);
            AllureManager.addStep(operationType + " executed successfully. Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            String errorMsg = "❌ Failed to execute " + operationType + " query: " + e.getMessage();
            System.err.println(errorMsg);
            AllureManager.addStep(operationType + " execution failed: " + e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }

        return rowsAffected;
    }

    /**
     * Insert data into a table
     */
    @Step("Insert data into table: {tableName}")
    public static int insertData(String tableName, Map<String, Object> data) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(entry.getKey());
            values.append("?");
            parameters.add(entry.getValue());
        }

        String query = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName, columns.toString(), values.toString());

        return executeUpdateQuery(query, "INSERT", parameters.toArray());
    }

    /**
     * Update data in a table
     */
    @Step("Update data in table: {tableName} where {whereClause}")
    public static int updateData(String tableName, Map<String, Object> data, String whereClause,
            Object... whereParameters) {
        StringBuilder setClause = new StringBuilder();
        List<Object> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(entry.getKey()).append(" = ?");
            parameters.add(entry.getValue());
        }

        // Add WHERE parameters
        Collections.addAll(parameters, whereParameters);

        String query = String.format("UPDATE %s SET %s WHERE %s",
                tableName, setClause.toString(), whereClause);

        return executeUpdateQuery(query, "UPDATE", parameters.toArray());
    }

    /**
     * Delete data from a table
     */
    @Step("Delete data from table: {tableName} where {whereClause}")
    public static int deleteData(String tableName, String whereClause, Object... parameters) {
        String query = String.format("DELETE FROM %s WHERE %s", tableName, whereClause);
        return executeUpdateQuery(query, "DELETE", parameters);
    }

    /**
     * Check if a record exists
     */
    @Step("Check if record exists in table: {tableName} where {whereClause}")
    public static boolean recordExists(String tableName, String whereClause, Object... parameters) {
        String query = String.format("SELECT 1 FROM %s WHERE %s LIMIT 1", tableName, whereClause);
        List<Map<String, Object>> results = executeSelectQuery(query, parameters);

        boolean exists = !results.isEmpty();
        System.out.println("🔍 Record existence check: " + (exists ? "EXISTS" : "NOT EXISTS"));
        AllureManager.addStep("Record " + (exists ? "exists" : "does not exist") + " in table: " + tableName);

        return exists;
    }

    /**
     * Get record count
     */
    @Step("Count records in table: {tableName}")
    public static int getRecordCount(String tableName, String whereClause, Object... parameters) {
        String query = whereClause != null && !whereClause.isEmpty()
                ? String.format("SELECT COUNT(*) as count FROM %s WHERE %s", tableName, whereClause)
                : String.format("SELECT COUNT(*) as count FROM %s", tableName);

        List<Map<String, Object>> results = executeSelectQuery(query, parameters);
        int count = results.isEmpty() ? 0 : ((Number) results.get(0).get("count")).intValue();

        System.out.println("📊 Record count: " + count);
        AllureManager.addStep("Record count: " + count);

        return count;
    }

    /**
     * Validate data in database
     */
    @Step("Validate data in table: {tableName}")
    public static boolean validateData(String tableName, String whereClause, Map<String, Object> expectedData,
            Object... whereParameters) {
        List<Map<String, Object>> results = executeSelectQuery(
                String.format("SELECT * FROM %s WHERE %s", tableName, whereClause),
                whereParameters);

        if (results.isEmpty()) {
            System.err.println("❌ No records found for validation");
            AllureManager.addStep("Validation failed: No records found");
            return false;
        }

        Map<String, Object> actualData = results.get(0);
        boolean isValid = true;

        for (Map.Entry<String, Object> expected : expectedData.entrySet()) {
            String column = expected.getKey();
            Object expectedValue = expected.getValue();
            Object actualValue = actualData.get(column);

            if (!Objects.equals(expectedValue, actualValue)) {
                System.err.println("❌ Validation failed for column '" + column + "': " +
                        "Expected=" + expectedValue + ", Actual=" + actualValue);
                AllureManager.addStep(
                        "Validation failed for " + column + ": Expected=" + expectedValue + ", Actual=" + actualValue);
                isValid = false;
            }
        }

        if (isValid) {
            System.out.println("✅ Data validation passed for all columns");
            AllureManager.addStep("Data validation passed for all columns");
        }

        return isValid;
    }

    /**
     * Execute DDL scripts (CREATE, DROP, ALTER)
     */
    @Step("Execute DDL script: {description}")
    public static void executeDDL(String script, String description) {
        try (Connection conn = DatabaseFactory.getDatabaseConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute(script);
            System.out.println("✅ DDL executed successfully: " + description);
            AllureManager.addStep("DDL executed: " + description);

        } catch (SQLException e) {
            String errorMsg = "❌ Failed to execute DDL: " + e.getMessage();
            System.err.println(errorMsg);
            AllureManager.addStep("DDL execution failed: " + e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Truncate table (remove all data)
     */
    @Step("Truncate table: {tableName}")
    public static void truncateTable(String tableName) {
        String query = "TRUNCATE TABLE " + tableName;
        executeDDL(query, "Truncate table: " + tableName);
    }

    /**
     * Get table structure information
     */
    @Step("Get table structure: {tableName}")
    public static List<Map<String, Object>> getTableStructure(String tableName) {
        String query = DatabaseFactory.getDatabaseType().equalsIgnoreCase("postgresql")
                ? "SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name = ?"
                : "DESCRIBE " + tableName;

        return executeSelectQuery(query, tableName);
    }

    /**
     * Set parameters in PreparedStatement
     */
    private static void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            if (param == null) {
                stmt.setNull(i + 1, Types.NULL);
            } else if (param instanceof String) {
                stmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Long) {
                stmt.setLong(i + 1, (Long) param);
            } else if (param instanceof Double) {
                stmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof Boolean) {
                stmt.setBoolean(i + 1, (Boolean) param);
            } else if (param instanceof java.util.Date) {
                stmt.setDate(i + 1, new java.sql.Date(((java.util.Date) param).getTime()));
            } else if (param instanceof Timestamp) {
                stmt.setTimestamp(i + 1, (Timestamp) param);
            } else {
                stmt.setObject(i + 1, param);
            }
        }
    }

    /**
     * Print query results in a formatted table
     */
    public static void printResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            System.out.println("📋 No results to display");
            return;
        }

        System.out.println("📋 Query Results:");
        System.out.println("=" + "=".repeat(80));

        // Print headers
        Set<String> columns = results.get(0).keySet();
        for (String column : columns) {
            System.out.printf("%-20s | ", column);
        }
        System.out.println();
        System.out.println("-".repeat(80));

        // Print data
        for (Map<String, Object> row : results) {
            for (String column : columns) {
                Object value = row.get(column);
                System.out.printf("%-20s | ", value != null ? value.toString() : "NULL");
            }
            System.out.println();
        }
        System.out.println("=" + "=".repeat(80));
        System.out.println("Total rows: " + results.size());
    }
}