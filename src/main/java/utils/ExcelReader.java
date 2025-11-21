package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelReader - utility to read Excel files into a List of Maps
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
    public List<Map<String, String>> readExcelData(String filePath, String sheetName) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            System.err.println("Excel file not found: " + filePath);
            return data;
        }

        try (InputStream is = new FileInputStream(path.toFile()); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                System.err.println("Sheet not found: " + sheetName);
                return data; // Return an empty list if the sheet doesn't exist
            }

            // Get the header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                System.err.println("Header row is missing in sheet: " + sheetName);
                return data;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getStringValueFromCell(cell));
            }

            // Iterate over the data rows (starting from the second row, index 1)
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row dataRow = sheet.getRow(rowIndex);
                // Skip null rows
                if (dataRow == null) {
                    continue;
                }
                Map<String, String> rowData = new HashMap<>();
                for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                    Cell cell = dataRow.getCell(cellIndex);
                    String header = headers.get(cellIndex);
                    String cellValue = getStringValueFromCell(cell);
                    rowData.put(header, cellValue);
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            // Wrap other exceptions as IOException to keep the signature simple
            throw new IOException("Failed to read Excel file: " + filePath, e);
        }

        return data;
    }

    /**
     * Helper method to get the string value of a cell, regardless of its type.
     * Handles null cells gracefully.
     *
     * @param cell The cell to get the value from.
     * @return The string representation of the cell's value.
     */
    private String getStringValueFromCell(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                } else {
                    double num = cell.getNumericCellValue();
                    // Format to remove trailing .0 when not needed
                    if (num == (long) num) {
                        return String.valueOf((long) num);
                    }
                    return String.valueOf(num);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Try to evaluate formula result as string where possible
                try {
                    return cell.getStringCellValue();
                } catch (Exception ex) {
                    try {
                        double num = cell.getNumericCellValue();
                        return String.valueOf(num);
                    } catch (Exception ex2) {
                        return cell.toString();
                    }
                }
            case BLANK:
                return "";
            default:
                return cell.toString();
        }
    }
}