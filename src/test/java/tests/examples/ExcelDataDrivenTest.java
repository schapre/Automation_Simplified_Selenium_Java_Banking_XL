package tests.examples;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.Map;

import utils.DriverFactory;
import utils.ExcelUtils;
import utils.AllureManager;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

/**
 * Example test class demonstrating Excel data-driven testing
 * Uses @DataProvider to read test data from Excel files
 */
public class ExcelDataDrivenTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = DriverFactory.initWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        AllureManager.addStep("Browser initialized: Chrome");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            AllureManager.addStep("Browser closed");
        }
    }

    /**
     * Login test using Excel data provider
     */
    @Test(dataProvider = "loginTestData", dataProviderClass = ExcelUtils.class)
    @Description("Data-driven login test using Excel data")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithExcelData(Map<String, String> testData) {
        // Extract test data
        String testCaseName = testData.get("TestCaseName");
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedResult = testData.get("ExpectedResult");
        String browser = testData.get("Browser");

        AllureManager.addStep("Executing test case: " + testCaseName);
        AllureManager.addStep("Test data - Username: " + username + ", Browser: " + browser);

        // Navigate to login page
        navigateToLoginPage();

        // Perform login
        performLogin(username, password);

        // Verify result based on expected outcome
        if ("Success".equals(expectedResult)) {
            verifySuccessfulLogin();
        } else {
            verifyFailedLogin();
        }

        // Update test result in Excel (optional)
        updateTestResultInExcel(testCaseName, "PASS");
    }

    /**
     * Web test using custom Excel data provider
     */
    @Test(dataProvider = "webTestData", dataProviderClass = ExcelUtils.class)
    @Description("Web functionality test using Excel data")
    @Severity(SeverityLevel.NORMAL)
    public void testWebFunctionalityWithExcelData(Map<String, String> testData) {
        String testCaseName = testData.get("TestCaseName");
        String url = testData.get("URL");
        String username = testData.get("Username");
        String password = testData.get("Password");
        String expectedTitle = testData.get("ExpectedTitle");

        AllureManager.addStep("Executing web test: " + testCaseName);

        // Navigate to URL
        driver.get(url);
        AllureManager.addStep("Navigated to: " + url);

        // Perform actions based on test case
        if (testCaseName.contains("Login")) {
            performLogin(username, password);

            if ("Success".equals(testData.get("Status")) || "PASS".equals(testData.get("Status"))) {
                verifySuccessfulLogin();
            }
        }

        // Verify page title (if specified)
        if (expectedTitle != null && !expectedTitle.isEmpty()) {
            verifyPageTitle(expectedTitle);
        }
    }

    /**
     * Parameterized test using specific test case data
     */
    @Test
    @Description("Single test case using specific Excel data lookup")
    @Severity(SeverityLevel.NORMAL)
    public void testSpecificTestCase() {
        // Get specific test case data
        Map<String, String> testData = ExcelUtils.getTestCaseData(
                "src/test/resources/testdata/testdata.xlsx",
                "LoginData",
                "LoginTest_Valid");

        if (!testData.isEmpty()) {
            String username = testData.get("Username");
            String password = testData.get("Password");

            AllureManager.addStep("Using specific test case: LoginTest_Valid");

            navigateToLoginPage();
            performLogin(username, password);
            verifySuccessfulLogin();
        } else {
            Assert.fail("Test data not found for LoginTest_Valid");
        }
    }

    /**
     * Test using column data from Excel
     */
    @Test
    @Description("Test using column data extraction from Excel")
    @Severity(SeverityLevel.MINOR)
    public void testWithColumnData() {
        // Get all usernames from Excel
        var usernames = ExcelUtils.getColumnData(
                "src/test/resources/testdata/testdata.xlsx",
                "LoginData",
                "Username");

        Assert.assertFalse(usernames.isEmpty(), "No usernames found in Excel data");
        AllureManager.addStep("Found " + usernames.size() + " usernames in Excel data");

        // Test with the first valid username
        String firstUsername = usernames.stream()
                .filter(u -> !u.isEmpty())
                .findFirst()
                .orElse("Admin");

        AllureManager.addStep("Testing with username: " + firstUsername);

        navigateToLoginPage();
        performLogin(firstUsername, "admin123");
        verifySuccessfulLogin();
    }

    // Helper methods

    @Step("Navigate to login page")
    private void navigateToLoginPage() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        AllureManager.addStep("Login page loaded successfully");
    }

    @Step("Perform login with username: {username}")
    private void performLogin(String username, String password) {
        try {
            WebElement usernameField = wait.until(ExpectedConditions.elementToBeClickable(By.name("username")));
            WebElement passwordField = driver.findElement(By.name("password"));
            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));

            usernameField.clear();
            usernameField.sendKeys(username);

            passwordField.clear();
            passwordField.sendKeys(password);

            loginButton.click();

            AllureManager.addStep("Login attempted with credentials");

            // Wait for page to load
            Thread.sleep(2000);

        } catch (Exception e) {
            AllureManager.addStep("Login failed: " + e.getMessage());
            throw new RuntimeException("Login operation failed", e);
        }
    }

    @Step("Verify successful login")
    private void verifySuccessfulLogin() {
        try {
            // Check for dashboard or successful login indicators
            boolean isLoggedIn = wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("dashboard"),
                    ExpectedConditions.presenceOfElementLocated(By.className("oxd-userdropdown")),
                    ExpectedConditions
                            .presenceOfElementLocated(By.xpath("//span[contains(@class,'oxd-userdropdown-tab')]"))));

            Assert.assertTrue(isLoggedIn, "Login was not successful - dashboard not found");
            AllureManager.addStep("✅ Login verification successful");

        } catch (Exception e) {
            AllureManager.addStep("❌ Login verification failed: " + e.getMessage());
            Assert.fail("Failed to verify successful login: " + e.getMessage());
        }
    }

    @Step("Verify failed login")
    private void verifyFailedLogin() {
        try {
            // Check for error messages or staying on login page
            boolean hasError = wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.className("oxd-alert")),
                    ExpectedConditions
                            .presenceOfElementLocated(By.xpath("//p[contains(@class,'oxd-alert-content-text')]")),
                    ExpectedConditions.urlContains("auth/login")));

            Assert.assertTrue(hasError, "Expected login to fail but no error indicators found");
            AllureManager.addStep("✅ Login failure verification successful");

        } catch (Exception e) {
            AllureManager.addStep("❌ Login failure verification failed: " + e.getMessage());
            Assert.fail("Failed to verify login failure: " + e.getMessage());
        }
    }

    @Step("Verify page title contains: {expectedTitle}")
    private void verifyPageTitle(String expectedTitle) {
        String actualTitle = driver.getTitle();
        Assert.assertTrue(actualTitle.contains(expectedTitle),
                "Page title verification failed. Expected: " + expectedTitle + ", Actual: " + actualTitle);
        AllureManager.addStep("✅ Page title verified: " + actualTitle);
    }

    @Step("Update test result in Excel for: {testCaseName}")
    private void updateTestResultInExcel(String testCaseName, String status) {
        try {
            ExcelUtils.updateTestResult(
                    "src/test/resources/testdata/testdata.xlsx",
                    "LoginData",
                    testCaseName,
                    status,
                    "Automated test executed on " + java.time.LocalDateTime.now());
            AllureManager.addStep("Test result updated in Excel: " + status);
        } catch (Exception e) {
            AllureManager.addStep("Failed to update Excel: " + e.getMessage());
            // Don't fail the test just because Excel update failed
        }
    }
}