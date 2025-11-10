# 📊 Excel Integration Guide

 

The framework now includes comprehensive Excel data sheet automation for data-driven testing. This integration allows you to:

- 📁 Store test data in Excel files
- 🔄 Use TestNG DataProvider with Excel data
- ⚙️ Configure Excel settings via properties
- 📝 Update test results back to Excel
- 🔍 Query specific test cases by name
- 📊 Extract column data for analysis

 


 


The framework supports multiple Excel files for different test types:


src/test/resources/testdata/
├── web_test_data.xlsx      # Web automation test data
├── mobile_test_data.xlsx   # Mobile automation test data  
├── api_test_data.xlsx      # API testing test data
└── testdata.xlsx           # General/Login test data


 



// Using DataProvider in test class
@Test(dataProvider = "loginTestData", dataProviderClass = ExcelUtils.class)
public void testLogin(Map<String, String> testData) {
    String username = testData.get("Username");
    String password = testData.get("Password");
    // ... test logic
}

// Get specific test case data
Map<String, String> data = ExcelUtils.getTestCaseData(
    "src/test/resources/testdata/testdata.xlsx", 
    "LoginData", 
    "LoginTest_Valid"
);

// Get column data
List<String> usernames = ExcelUtils.getColumnData(
    "src/test/resources/testdata/testdata.xlsx", 
    "LoginData", 
    "Username"
);


 


 

|--------------|----------|----------|---------------|---------|----------|---------|----------|


 

|--------------|-----|----------|----------|---------------|---------|---------|---------|----------|----------|


 

|--------------|----------|------------|------------|-------------|----------|----------|---------------|---------|----------|


 

|--------------|--------|----------|-------------|---------------|------------------|---------|-----------|---------|----------|


 


Configure Excel settings in `config.properties`:


# Excel Data Configuration
excel.testdata.path=src/test/resources/testdata/
excel.web.testdata=web_test_data.xlsx
excel.mobile.testdata=mobile_test_data.xlsx
excel.api.testdata=api_test_data.xlsx
excel.login.testdata=testdata.xlsx

# Excel Sheet Names
excel.web.sheetname=WebTests
excel.mobile.sheetname=MobileTests
excel.api.sheetname=ApiTests
excel.login.sheetname=LoginData

# Excel Data-driven Testing
excel.dataprovider.enabled=true
excel.update.results=true
excel.backup.original=true


 



|------------------|-------------|------------|------------|


 


 

// Read all Excel data
List<Map<String, String>> data = ExcelReader.readExcelData(filePath, sheetName);

// Get specific test case
Map<String, String> testCase = ExcelUtils.getTestCaseData(filePath, sheetName, testCaseName);

// Get column data
List<String> columnValues = ExcelUtils.getColumnData(filePath, sheetName, columnName);

// Get DataProvider array
Object[][] dataProvider = ExcelUtils.getExcelDataProvider(filePath, sheetName);


 

// Write data to Excel
ExcelUtils.writeDataToExcel(filePath, sheetName, dataMap, rowIndex);

// Update test result
ExcelUtils.updateTestResult(filePath, sheetName, testCaseName, status, comments);

// Create new Excel file
ExcelUtils.createExcelFile(filePath, sheetName, headers, sampleData);


 

// Get configured paths and settings
String path = ExcelUtils.ExcelConfig.getTestDataPath();
String webFile = ExcelUtils.ExcelConfig.getWebTestDataFile();
boolean enabled = ExcelUtils.ExcelConfig.isDataProviderEnabled();


 



public class ExcelDataDrivenTest {
    
    @Test(dataProvider = "loginTestData", dataProviderClass = ExcelUtils.class)
    public void testLoginWithExcelData(Map<String, String> testData) {
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("ExpectedResult");
        
        // Perform login
        performLogin(username, password);
        
        // Verify based on expected result
        if ("Success".equals(expectedResult)) {
            verifySuccessfulLogin();
        } else {
            verifyFailedLogin();
        }
        
        // Update result in Excel
        ExcelUtils.updateTestResult(
            ExcelUtils.ExcelConfig.getFullPath(ExcelUtils.ExcelConfig.getLoginTestDataFile()),
            ExcelUtils.ExcelConfig.getLoginSheetName(),
            testData.get("TestCaseName"),
            "PASS",
            "Test executed successfully"
        );
    }
}


 


 

# Run login tests with Excel data
mvn test -Dtest=ExcelDataDrivenTest#testLoginWithExcelData

# Run all Excel data-driven tests
mvn test -Dtest=ExcelDataDrivenTest


 

<test name="Excel Data Driven Tests">
    <classes>
        <class name="tests.examples.ExcelDataDrivenTest"/>
    </classes>
</test>


 


 

- Use descriptive sheet names
- Include TestCaseName column for identification
- Add Status and Comments columns for result tracking
- Keep headers consistent across similar test types

 

- Store sensitive data separately or encrypted
- Use meaningful test case names
- Group related test cases in same sheet
- Maintain data consistency

 

try {
    Map<String, String> testData = ExcelUtils.getTestCaseData(filePath, sheetName, testCaseName);
    if (testData.isEmpty()) {
        Assert.fail("Test data not found for: " + testCaseName);
    }
} catch (Exception e) {
    AllureManager.addStep("Failed to read Excel data: " + e.getMessage());
    throw e;
}


 

- Use properties file for Excel settings
- Enable/disable DataProviders via configuration
- Maintain separate Excel files per test type

 


 


1. **File Not Found**
   - Check file path in configuration
   - Ensure Excel files exist in testdata folder

2. **Sheet Not Found**
   - Verify sheet names in Excel files
   - Check sheet name configuration

3. **Empty Data**
   - Ensure Excel files have data rows
   - Check column headers match expected names

4. **ClassPath Issues**
   - Ensure Apache POI dependencies are included
   - Check Maven dependencies

 

// Enable debug logging
System.out.println("Excel file path: " + ExcelUtils.ExcelConfig.getFullPath("testdata.xlsx"));
System.out.println("Sheet name: " + ExcelUtils.ExcelConfig.getLoginSheetName());


 


Required Maven dependencies (already included):

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>


 


- ✅ ExcelReader class activated
- ✅ ExcelUtils with DataProvider methods
- ✅ Configuration-based Excel settings
- ✅ Multiple DataProvider support
- ✅ Test result update functionality
- ✅ Example test class
- ✅ Comprehensive documentation

Your Excel integration is now **fully functional**! 🎉
