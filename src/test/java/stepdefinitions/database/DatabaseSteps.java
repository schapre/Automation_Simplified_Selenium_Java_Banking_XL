package stepdefinitions.database;

import io.cucumber.java.en.*;
import io.qameta.allure.Step;
import utils.DatabaseUtils;
import utils.AllureManager;
import utils.MessageFormatter;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Database Step Definitions for Cucumber Tests
 * Provides common database testing operations
 */
public class DatabaseSteps {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSteps.class);

    private List<Map<String, Object>> queryResults;
    private int recordsAffected;
    private boolean operationResult;

    @Given("I have a database connection")
    @Step("Verify database connection is available")
    public void iHaveADatabaseConnection() {
        // Connection is already established in Hooks
        logger.debug(MessageFormatter.getDatabaseMessage("connection.verifying"));
        logger.info(MessageFormatter.getDatabaseMessage("connection.verified"));
        AllureManager.addStep("Database connection verified");
    }

    @When("I execute the query {string}")
    @Step("Execute database query: {query}")
    public void iExecuteTheQuery(String query) {
        logger.debug(MessageFormatter.getDatabaseMessage("query.executing", query));
        queryResults = DatabaseUtils.executeSelectQuery(query);
        logger.info(MessageFormatter.getDatabaseMessage("query.executed", queryResults.size()));
        AllureManager.addStep("Query executed with " + queryResults.size() + " results");
    }

    @When("I execute the query {string} with parameters:")
    @Step("Execute parameterized query: {query}")
    public void iExecuteTheQueryWithParameters(String query, io.cucumber.datatable.DataTable dataTable) {
        List<String> parameters = dataTable.asList();
        Object[] params = parameters.toArray();
        logger.debug(MessageFormatter.getDatabaseMessage("query.parameterized.executing", params.length));
        queryResults = DatabaseUtils.executeSelectQuery(query, params);
        logger.info(MessageFormatter.getDatabaseMessage("query.parameterized.executed", queryResults.size()));
        AllureManager.addStep("Parameterized query executed with " + queryResults.size() + " results");
    }

    @When("I insert data into table {string} with values:")
    @Step("Insert data into table: {tableName}")
    public void iInsertDataIntoTableWithValues(String tableName, io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> rawData = dataTable.asMap(String.class, String.class);
        Map<String, Object> data = new HashMap<>();

        // Convert string values to appropriate types
        for (Map.Entry<String, String> entry : rawData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Try to convert to appropriate type
            if (value.matches("\\d+")) {
                data.put(key, Integer.parseInt(value));
            } else if (value.matches("\\d+\\.\\d+")) {
                data.put(key, Double.parseDouble(value));
            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                data.put(key, Boolean.parseBoolean(value));
            } else {
                data.put(key, value);
            }
        }

        logger.debug(MessageFormatter.getDatabaseMessage("insert.executing", tableName));
        recordsAffected = DatabaseUtils.insertData(tableName, data);
        logger.info(MessageFormatter.getDatabaseMessage("insert.executed", recordsAffected, tableName));
        AllureManager.addStep("Inserted " + recordsAffected + " record(s) into " + tableName);
    }

    @When("I update table {string} set {string} where {string}")
    @Step("Update data in table: {tableName}")
    public void iUpdateTableSetWhere(String tableName, String setClause, String whereClause) {
        // Parse SET clause
        Map<String, Object> data = new HashMap<>();
        String[] setPairs = setClause.split(",");
        for (String pair : setPairs) {
            String[] keyValue = pair.trim().split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replaceAll("'", "");
                data.put(key, value);
            }
        }

        recordsAffected = DatabaseUtils.updateData(tableName, data, whereClause);
        System.out.println("🔄 Updated " + recordsAffected + " record(s) in " + tableName);
        AllureManager.addStep("Updated " + recordsAffected + " record(s) in " + tableName);
    }

    @When("I delete from table {string} where {string}")
    @Step("Delete data from table: {tableName}")
    public void iDeleteFromTableWhere(String tableName, String whereClause) {
        recordsAffected = DatabaseUtils.deleteData(tableName, whereClause);
        System.out.println("🗑️ Deleted " + recordsAffected + " record(s) from " + tableName);
        AllureManager.addStep("Deleted " + recordsAffected + " record(s) from " + tableName);
    }

    @When("I check if record exists in table {string} where {string}")
    @Step("Check record existence in table: {tableName}")
    public void iCheckIfRecordExistsInTableWhere(String tableName, String whereClause) {
        operationResult = DatabaseUtils.recordExists(tableName, whereClause);
        System.out.println("🔍 Record " + (operationResult ? "exists" : "does not exist") + " in " + tableName);
        AllureManager.addStep("Record existence check: " + (operationResult ? "EXISTS" : "NOT EXISTS"));
    }

    @When("I count records in table {string}")
    @Step("Count all records in table: {tableName}")
    public void iCountRecordsInTable(String tableName) {
        recordsAffected = DatabaseUtils.getRecordCount(tableName, null);
        logger.info("📊 Total records in {}", tableName + ": " + recordsAffected);
        AllureManager.addStep("Total records: " + recordsAffected);
    }

    @When("I count records in table {string} where {string}")
    @Step("Count filtered records in table: {tableName}")
    public void iCountRecordsInTableWhere(String tableName, String whereClause) {
        recordsAffected = DatabaseUtils.getRecordCount(tableName, whereClause);
        logger.info("📊 Filtered records in {}", tableName + ": " + recordsAffected);
        AllureManager.addStep("Filtered records: " + recordsAffected);
    }

    @When("I truncate table {string}")
    @Step("Truncate table: {tableName}")
    public void iTruncateTable(String tableName) {
        DatabaseUtils.truncateTable(tableName);
        logger.info("🧹 Table {}", tableName + " truncated");
        AllureManager.addStep("Table truncated: " + tableName);
    }

    @Then("I should get {int} results")
    @Step("Verify result count: {expectedCount}")
    public void iShouldGetResults(int expectedCount) {
        int actualCount = queryResults != null ? queryResults.size() : 0;
        logger.info("✅ Expected: {}", expectedCount + ", Actual: " + actualCount);
        AllureManager.addStep("Result count verification - Expected: " + expectedCount + ", Actual: " + actualCount);
        Assert.assertEquals(actualCount, expectedCount, "Query result count mismatch");
    }

    @Then("the results should contain:")
    @Step("Verify query results contain expected data")
    public void theResultsShouldContain(io.cucumber.datatable.DataTable dataTable) {
        Assert.assertNotNull(queryResults, "No query results available");
        Assert.assertFalse(queryResults.isEmpty(), "Query results are empty");

        List<Map<String, String>> expectedData = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> expectedRow : expectedData) {
            boolean rowFound = false;

            for (Map<String, Object> actualRow : queryResults) {
                boolean rowMatches = true;

                for (Map.Entry<String, String> expectedField : expectedRow.entrySet()) {
                    String fieldName = expectedField.getKey();
                    String expectedValue = expectedField.getValue();
                    Object actualValue = actualRow.get(fieldName);

                    if (actualValue == null || !expectedValue.equals(actualValue.toString())) {
                        rowMatches = false;
                        break;
                    }
                }

                if (rowMatches) {
                    rowFound = true;
                    break;
                }
            }

            Assert.assertTrue(rowFound, "Expected row not found in results: " + expectedRow);
        }

        logger.info("✅ All expected data found in query results");
        AllureManager.addStep("All expected data verified in query results");
    }

    @Then("{int} records should be affected")
    @Step("Verify affected record count: {expectedCount}")
    public void recordsShouldBeAffected(int expectedCount) {
        logger.info("✅ Expected: {}", expectedCount + ", Actual: " + recordsAffected);
        AllureManager
                .addStep("Affected records verification - Expected: " + expectedCount + ", Actual: " + recordsAffected);
        Assert.assertEquals(recordsAffected, expectedCount, "Affected records count mismatch");
    }

    @Then("the record should exist")
    @Step("Verify record exists")
    public void theRecordShouldExist() {
        logger.info("✅ Record existence verified: {}", operationResult);
        AllureManager.addStep("Record existence verified: " + operationResult);
        Assert.assertTrue(operationResult, "Expected record to exist but it doesn't");
    }

    @Then("the record should not exist")
    @Step("Verify record does not exist")
    public void theRecordShouldNotExist() {
        logger.info("✅ Record non-existence verified: {}", !operationResult);
        AllureManager.addStep("Record non-existence verified: " + !operationResult);
        Assert.assertFalse(operationResult, "Expected record to not exist but it does");
    }

    @Then("I should get count {int}")
    @Step("Verify record count: {expectedCount}")
    public void iShouldGetCount(int expectedCount) {
        logger.info("✅ Expected count: {}", expectedCount + ", Actual count: " + recordsAffected);
        AllureManager.addStep("Count verification - Expected: " + expectedCount + ", Actual: " + recordsAffected);
        Assert.assertEquals(recordsAffected, expectedCount, "Record count mismatch");
    }

    @Then("I validate data in table {string} where {string} has values:")
    @Step("Validate data in table: {tableName}")
    public void iValidateDataInTableWhereHasValues(String tableName, String whereClause,
            io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> rawExpectedData = dataTable.asMap(String.class, String.class);
        Map<String, Object> expectedData = new HashMap<>();

        // Convert string values to appropriate types
        for (Map.Entry<String, String> entry : rawExpectedData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Try to convert to appropriate type
            if (value.matches("\\d+")) {
                expectedData.put(key, Integer.parseInt(value));
            } else if (value.matches("\\d+\\.\\d+")) {
                expectedData.put(key, Double.parseDouble(value));
            } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                expectedData.put(key, Boolean.parseBoolean(value));
            } else {
                expectedData.put(key, value);
            }
        }

        boolean isValid = DatabaseUtils.validateData(tableName, whereClause, expectedData);
        System.out.println("✅ Data validation result: " + (isValid ? "PASSED" : "FAILED"));
        AllureManager.addStep("Data validation: " + (isValid ? "PASSED" : "FAILED"));
        Assert.assertTrue(isValid, "Data validation failed for table: " + tableName);
    }

    @And("I print the query results")
    @Step("Print query results to console")
    public void iPrintTheQueryResults() {
        if (queryResults != null) {
            DatabaseUtils.printResults(queryResults);
            AllureManager.addStep("Query results printed to console");
        } else {
            logger.info("📋 No query results available to print");
            AllureManager.addStep("No query results available");
        }
    }
}