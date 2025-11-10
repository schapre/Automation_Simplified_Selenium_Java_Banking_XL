package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import io.qameta.allure.Step;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils - Advanced Excel operations and TestNG DataProvider utilities
 * Provides data-driven testing support with Excel integration
 */
public class ExcelUtils {

    private static ExcelReader excelReader = new ExcelReader();

    /**
     * Generic DataProvider method for TestNG tests
     * Reads Excel data and converts to Object[][] for TestNG
     *
     * @param filePath  Excel file path
     * @param sheetName Sheet name to read
     * @return Object[][] Data for TestNG DataProvider
     */
    public static Object[][] getExcelDataProvider(String filePath, String sheetName) {
        try {
            List<Map<String, String>> excelData = excelReader.readExcelData(filePath, sheetName);

            if (excelData.isEmpty()) {
                System.err.println("No data found in Excel sheet: " + sheetName);
                return new Object[0][0];
            }

            // Convert List of Maps to Object[][]
            Object[][] dataProvider = new Object[excelData.size()][];

            for (int i = 0; i < excelData.size(); i++) {
                Map<String, String> row = excelData.get(i);
                dataProvider[i] = new Object[] { row };
            }

            System.out.println("✅ Loaded " + excelData.size() + " test data rows from: " + sheetName);
            return dataProvider;

        } catch (IOException e) {
            System.err.println("❌ Failed to read Excel file: " + filePath);
            e.printStackTrace();
            return new Object[0][0];
        }
    }

    /**
     * DataProvider for Web test data
     */
    @DataProvider(name = "webTestData")
    public static Object[][] getWebTestData() {
        if (!ExcelConfig.isDataProviderEnabled()) {
            return new Object[0][0];
        }
        String filePath = ExcelConfig.getFullPath(ExcelConfig.getWebTestDataFile());
        return getExcelDataProvider(filePath, ExcelConfig.getWebSheetName());
    }

    /**
     * DataProvider for Mobile test data
     */
    @DataProvider(name = "mobileTestData")
    public static Object[][] getMobileTestData() {
        if (!ExcelConfig.isDataProviderEnabled()) {
            return new Object[0][0];
        }
        String filePath = ExcelConfig.getFullPath(ExcelConfig.getMobileTestDataFile());
        return getExcelDataProvider(filePath, ExcelConfig.getMobileSheetName());
    }

    /**
     * DataProvider for API test data
     */
    @DataProvider(name = "apiTestData")
    public static Object[][] getApiTestData() {
        if (!ExcelConfig.isDataProviderEnabled()) {
            return new Object[0][0];
        }
        String filePath = ExcelConfig.getFullPath(ExcelConfig.getApiTestDataFile());
        return getExcelDataProvider(filePath, ExcelConfig.getApiSheetName());
    }

    /**
     * DataProvider for Login test data
     */
    @DataProvider(name = "loginTestData")
    public static Object[][] getLoginTestData() {
        if (!ExcelConfig.isDataProviderEnabled()) {
            return new Object[0][0];
        }
        String filePath = ExcelConfig.getFullPath(ExcelConfig.getLoginTestDataFile());
        return getExcelDataProvider(filePath, ExcelConfig.getLoginSheetName());
    }

    /**
     * Generic method to get specific test data by test case name
     *
     * @param filePath     Excel file path
     * @param sheetName    Sheet name
     * @param testCaseName Test case name to filter
     * @return Single test data row
     */
    @Step("Get test data for test case: {testCaseName}")
    public static Map<String, String> getTestCaseData(String filePath, String sheetName, String testCaseName) {
        try {
            List<Map<String, String>> excelData = excelReader.readExcelData(filePath, sheetName);

            for (Map<String, String> row : excelData) {
                if (testCaseName.equals(row.get("TestCaseName")) ||
                        testCaseName.equals(row.get("testCaseName")) ||
                        testCaseName.equals(row.get("TestCase"))) {
                    return row;
                }
            }

            System.err.println("❌ Test case not found: " + testCaseName);
            return new HashMap<>();

        } catch (IOException e) {
            System.err.println("❌ Failed to read test case data: " + testCaseName);
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Write data to Excel file (for test results or data updates)
     *
     * @param filePath  Excel file path
     * @param sheetName Sheet name
     * @param data      Data to write
     * @param rowIndex  Row index to write (0-based)
     */
    @Step("Write data to Excel: {filePath}")
    public static void writeDataToExcel(String filePath, String sheetName, Map<String, String> data, int rowIndex) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;

        try {
            fis = new FileInputStream(filePath);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            // Get or create header row
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();

            if (headerRow == null) {
                headerRow = sheet.createRow(0);
                int colIndex = 0;
                for (String key : data.keySet()) {
                    Cell cell = headerRow.createCell(colIndex++);
                    cell.setCellValue(key);
                    headers.add(key);
                }
            } else {
                for (Cell cell : headerRow) {
                    headers.add(cell.getStringCellValue());
                }
            }

            // Write data row
            Row dataRow = sheet.getRow(rowIndex + 1); // +1 because row 0 is header
            if (dataRow == null) {
                dataRow = sheet.createRow(rowIndex + 1);
            }

            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                String value = data.getOrDefault(header, "");
                Cell cell = dataRow.getCell(i);
                if (cell == null) {
                    cell = dataRow.createCell(i);
                }
                cell.setCellValue(value);
            }

            fos = new FileOutputStream(filePath);
            workbook.write(fos);
            System.out.println("✅ Data written successfully to: " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Failed to write data to Excel: " + filePath);
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
                if (fos != null)
                    fos.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Update test result in Excel file
     *
     * @param filePath     Excel file path
     * @param sheetName    Sheet name
     * @param testCaseName Test case name
     * @param status       Test status (PASS/FAIL)
     * @param comments     Additional comments
     */
    @Step("Update test result for: {testCaseName} - Status: {status}")
    public static void updateTestResult(String filePath, String sheetName, String testCaseName,
            String status, String comments) {
        try {
            List<Map<String, String>> excelData = excelReader.readExcelData(filePath, sheetName);

            for (int i = 0; i < excelData.size(); i++) {
                Map<String, String> row = excelData.get(i);
                if (testCaseName.equals(row.get("TestCaseName")) ||
                        testCaseName.equals(row.get("testCaseName"))) {

                    Map<String, String> updateData = new HashMap<>(row);
                    updateData.put("Status", status);
                    updateData.put("Comments", comments);
                    updateData.put("LastUpdated", java.time.LocalDateTime.now().toString());

                    writeDataToExcel(filePath, sheetName, updateData, i);
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to update test result: " + testCaseName);
            e.printStackTrace();
        }
    }

    /**
     * Get column data from Excel sheet
     *
     * @param filePath   Excel file path
     * @param sheetName  Sheet name
     * @param columnName Column name
     * @return List of column values
     */
    @Step("Get column data: {columnName}")
    public static List<String> getColumnData(String filePath, String sheetName, String columnName) {
        List<String> columnData = new ArrayList<>();

        try {
            List<Map<String, String>> excelData = excelReader.readExcelData(filePath, sheetName);

            for (Map<String, String> row : excelData) {
                String value = row.getOrDefault(columnName, "");
                columnData.add(value);
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to get column data: " + columnName);
            e.printStackTrace();
        }

        return columnData;
    }

    /**
     * Get Excel configuration from config.properties
     */
    public static class ExcelConfig {
        public static String getTestDataPath() {
            String path = ConfigReader.getProperty("excel.testdata.path");
            return path != null ? path : "src/test/resources/testdata/";
        }

        public static String getWebTestDataFile() {
            String file = ConfigReader.getProperty("excel.web.testdata");
            return file != null ? file : "web_test_data.xlsx";
        }

        public static String getMobileTestDataFile() {
            String file = ConfigReader.getProperty("excel.mobile.testdata");
            return file != null ? file : "mobile_test_data.xlsx";
        }

        public static String getApiTestDataFile() {
            String file = ConfigReader.getProperty("excel.api.testdata");
            return file != null ? file : "api_test_data.xlsx";
        }

        public static String getLoginTestDataFile() {
            String file = ConfigReader.getProperty("excel.login.testdata");
            return file != null ? file : "testdata.xlsx";
        }

        public static String getWebSheetName() {
            String sheet = ConfigReader.getProperty("excel.web.sheetname");
            return sheet != null ? sheet : "WebTests";
        }

        public static String getMobileSheetName() {
            String sheet = ConfigReader.getProperty("excel.mobile.sheetname");
            return sheet != null ? sheet : "MobileTests";
        }

        public static String getApiSheetName() {
            String sheet = ConfigReader.getProperty("excel.api.sheetname");
            return sheet != null ? sheet : "ApiTests";
        }

        public static String getLoginSheetName() {
            String sheet = ConfigReader.getProperty("excel.login.sheetname");
            return sheet != null ? sheet : "LoginData";
        }

        public static boolean isDataProviderEnabled() {
            String enabled = ConfigReader.getProperty("excel.dataprovider.enabled");
            return enabled != null ? Boolean.parseBoolean(enabled) : true;
        }

        public static boolean isUpdateResultsEnabled() {
            String update = ConfigReader.getProperty("excel.update.results");
            return update != null ? Boolean.parseBoolean(update) : true;
        }

        public static boolean isBackupOriginalEnabled() {
            String backup = ConfigReader.getProperty("excel.backup.original");
            return backup != null ? Boolean.parseBoolean(backup) : true;
        }

        public static String getFullPath(String fileName) {
            return getTestDataPath() + fileName;
        }
    }

    /**
     * Create a new Excel file with sample data
     *
     * @param filePath   File path for new Excel file
     * @param sheetName  Sheet name
     * @param headers    Column headers
     * @param sampleData Sample data rows
     */
    @Step("Create Excel file: {filePath}")
    public static void createExcelFile(String filePath, String sheetName,
            String[] headers, String[][] sampleData) {
        Workbook workbook = new XSSFWorkbook();
        FileOutputStream fos = null;

        try {
            Sheet sheet = workbook.createSheet(sheetName);

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);

                // Auto-size column
                sheet.autoSizeColumn(i);
            }

            // Create data rows
            for (int i = 0; i < sampleData.length; i++) {
                Row dataRow = sheet.createRow(i + 1);
                String[] rowData = sampleData[i];

                for (int j = 0; j < rowData.length && j < headers.length; j++) {
                    Cell cell = dataRow.createCell(j);
                    cell.setCellValue(rowData[j]);
                }
            }

            // Auto-size all columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            fos = new FileOutputStream(filePath);
            workbook.write(fos);
            System.out.println("✅ Excel file created successfully: " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Failed to create Excel file: " + filePath);
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}