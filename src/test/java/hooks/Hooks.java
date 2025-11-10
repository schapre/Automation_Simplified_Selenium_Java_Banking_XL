package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.WebDriver;
import io.appium.java_client.AppiumDriver;
import utils.DriverFactory;
import utils.ApiClientFactory;
import utils.ConfigReader;
import utils.AllureManager;
import utils.VideoRecorder;
import utils.VideoManager;
import io.qameta.allure.Allure;

public class Hooks {
    WebDriver webDriver;
    AppiumDriver mobileDriver;
    RequestSpecification apiClient;

    @Before
    public void setUp(Scenario scenario) {
        RestAssured.useRelaxedHTTPSValidation();
        System.out.println("✅ SSL validation relaxed for RestAssured.");

        // Load config
        ConfigReader.loadProperties("src/test/resources/config/config.properties");

        String platformConfig = ConfigReader.getProperty("platform");
        String[] platforms = platformConfig.split(",");

        // Start video recording for UI tests
        boolean hasUITest = false;
        for (String platform : platforms) {
            if (platform.trim().toUpperCase().equals("WEB") || platform.trim().toUpperCase().equals("MOBILE")) {
                hasUITest = true;
                break;
            }
        }

        if (hasUITest) {
            // Initialize video directory and start recording for UI-based tests
            VideoManager.initializeVideoDirectory();
            String testName = scenario.getName().replaceAll("[^a-zA-Z0-9\\s]", "");
            VideoManager.startRecording(testName);
            System.out.println("🎥 Video recording started for: " + scenario.getName());
        }

        for (String platform : platforms) {
            switch (platform.trim().toUpperCase()) {
                case "WEB":
                    webDriver = DriverFactory.initWebDriver();
                    break;
                case "MOBILE":
                    mobileDriver = DriverFactory.initMobileDriver();
                    break;
                case "API":
                    ApiClientFactory.initApiClient();
                    apiClient = ApiClientFactory.getApiClient();
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported platform: " + platform);
            }
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        String platformConfig = ConfigReader.getProperty("platform");
        String[] platforms = platformConfig.split(",");

        // Check if this is a UI test that needs video recording
        boolean hasUITest = false;
        for (String platform : platforms) {
            if (platform.trim().toUpperCase().equals("WEB") || platform.trim().toUpperCase().equals("MOBILE")) {
                hasUITest = true;
                break;
            }
        }

        // Stop video recording and attach to report
        if (hasUITest && VideoRecorder.isRecording()) {
            String videoPath = VideoManager.stopRecording(true);
            System.out.println("🎥 Video recording stopped for: " + scenario.getName());

            if (videoPath != null) {
                System.out.println("📹 Video saved at: " + videoPath);
            }
        }

        // Capture screenshot for failed scenarios
        if (scenario.isFailed()) {
            for (String platform : platforms) {
                switch (platform.trim().toUpperCase()) {
                    case "WEB":
                        if (webDriver != null) {
                            AllureManager.attachScreenshot(webDriver, "Failed Test Screenshot - " + scenario.getName());
                        }
                        break;
                    case "MOBILE":
                        if (mobileDriver != null) {
                            AllureManager.attachScreenshot(mobileDriver,
                                    "Failed Test Screenshot - " + scenario.getName());
                        }
                        break;
                }
            }

            // Add failure information to Allure
            AllureManager.attachText("Failure Info",
                    "Scenario: " + scenario.getName() + " failed at " + java.time.LocalDateTime.now());
            Allure.step("Test failed: " + scenario.getName());
        }

        for (String platform : platforms) {
            switch (platform.trim().toUpperCase()) {
                case "WEB":
                case "MOBILE":
                    DriverFactory.quitDriver();
                    break;
                case "API":
                    ApiClientFactory.resetApiClient();
                    break;
            }
        }
    }
}
