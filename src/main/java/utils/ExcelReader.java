package utils;

// Note: Imports are commented out since the class methods are commented out
// Uncomment these imports when implementing the Excel reading functionality
/*
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*/

public class ExcelReader {

    /**
     * Reads data from an Excel file and returns it as a List of Maps.
     * Each Map represents a row, with keys being the column headers.
     *
     * @param filePath  The path to the Excel file.
     * @param sheetName The name of the sheet to read.
     * @return A List of Maps, where each Map is a row of data.
     * @throws IOException If there's an issue reading the file.
     */
    // public List<Map<String, String>> readExcelData(String filePath, String
    // sheetName) throws IOException {
    // List<Map<String, String>> data = new ArrayList<>();
    // FileInputStream fis = null;
    // Workbook workbook = null;
    // try {
    // fis = new FileInputStream(filePath);
    // workbook = new XSSFWorkbook(fis); // Use XSSFWorkbook for .xlsx files
    // Sheet sheet = workbook.getSheet(sheetName);
    // if (sheet == null) {
    // System.err.println("Sheet not found: " + sheetName);
    // return data; // Return an empty list if the sheet doesn't exist
    // }
    // // Get the header row
    // Row headerRow = sheet.getRow(0);
    // if (headerRow == null) {
    // System.err.println("Header row is missing in sheet: " + sheetName);
    // return data;
    // }
    // List<String> headers = new ArrayList<>();
    // for (Cell cell : headerRow) {
    // // Get cell value and add it to the headers list
    // headers.add(getStringValueFromCell(cell));
    // }
    // // Iterate over the data rows (starting from the second row, index 1)
    // for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
    // Row dataRow = sheet.getRow(rowIndex);
    // // Skip null rows
    // if (dataRow == null) {
    // continue;
    // }
    // Map<String, String> rowData = new HashMap<>();
    // for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
    // Cell cell = dataRow.getCell(cellIndex);
    // String header = headers.get(cellIndex);
    // String cellValue = getStringValueFromCell(cell);
    // rowData.put(header, cellValue);
    // }
    // data.add(rowData);
    // }
    // } finally {
    // // Close resources in a finally block to prevent resource leaks
    // if (workbook != null) {
    // ((FileInputStream) workbook).close();
    // }
    // if (fis != null) {
    // fis.close();
    // }
    // }
    // return data;
    // }
    /**
     * Helper method to get the string value of a cell, regardless of its type.
     * Handles null cells gracefully.
     *
     * @param cell The cell to get the value from.
     * @return The string representation of the cell's value.
     */
    // private String getStringValueFromCell(Cell cell) {
    // if (cell == null) {
    // return "";
    // }
    // switch (cell.getCellType()) {
    // case CellTypes.STRING:
    // return cell.getStringCellValue();
    // case CellTypes.NUMERIC:
    // // Check if it's a date or a number
    // if (DateUtil.isCellDateFormatted(cell)) {
    // return cell.getDateCellValue().toString();
    // } else {
    // return String.valueOf(cell.getNumericCellValue());
    // }
    // case CellTypes.BOOLEAN:
    // return String.valueOf(cell.getBooleanCellValue());
    // case CellTypes.FORMULA:
    // // Handle formula cells, evaluate to the result
    // return cell.getRichStringCellValue().getString(); // or evaluate formula
    // case CellTypes.BLANK:
    // return "";
    // default:
    // return cell.toString();
    // }
    // }
    // Example main method to demonstrate usage
    /*
     * public static void main(String[] args) {
     * ExcelReader reader = new ExcelReader();
     * String filePath = "src/test/resources/testdata/testdata.xlsx"; // Update with
     * the actual file path
     * String sheetName = "Sheet1";
     * try {
     * List<Map<String, String>> excelData = reader.readExcelData(filePath,
     * sheetName);
     * if (!excelData.isEmpty()) {
     * System.out.println("Excel Data:");
     * for (Map<String, String> row : excelData) {
     * System.out.println(row);
     * }
     * } else {
     * System.out.println("No data found or sheet is empty.");
     * }
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     */
}