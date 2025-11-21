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
import utils.MessageFormatter;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hooks {
    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);
    WebDriver webDriver;
    AppiumDriver mobileDriver;
    RequestSpecification apiClient;

    @Before
    public void setUp(Scenario scenario) {
        logger.debug(MessageFormatter.getMessage("test.scenario.setup", scenario.getName()));
        RestAssured.useRelaxedHTTPSValidation();
        logger.info(MessageFormatter.getMessage("auth.ssl.relaxed"));

        // Load config
        ConfigReader.loadProperties("src/test/resources/config/config.properties");

        String platformConfig = ConfigReader.getProperty("platform");
        String[] platforms = platformConfig.split(",");
        logger.debug(MessageFormatter.getMessage("test.platforms.detected", String.join(", ", platforms)));

        // Start video recording for UI tests
        boolean hasUITest = false;
        for (String platform : platforms) {
            if (platform.trim().toUpperCase().equals("WEB") || platform.trim().toUpperCase().equals("MOBILE")) {
                hasUITest = true;
                break;
            }
        }

        if (hasUITest) {
            // Initialize video directory and start MP4 recording for UI-based tests
            VideoManager.initializeVideoDirectory();
            String testName = scenario.getName().replaceAll("[^a-zA-Z0-9\\s]", "");

            // Check FFmpeg availability
            if (!VideoRecorder.isFFmpegAvailable()) {
                logger.warn("⚠️ FFmpeg not detected. Videos will be saved as frame sequences.");
                logger.warn("💡 Install FFmpeg to enable automatic MP4 video generation.");
            }

            VideoRecorder.startRecording(testName);
            logger.info("🎥 MP4 video recording started for: {}", scenario.getName());
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
            logger.info("🎥 Video recording stopped for: {}", scenario.getName());

            if (videoPath != null) {
                logger.info("📹 Video saved at: {}", videoPath);
            }
        }

        // Capture screenshot for failed scenarios
        if (scenario.isFailed()) {
            logger.warn(MessageFormatter.getMessage("test.scenario.failed", scenario.getName()));
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
        } else {
            logger.debug(MessageFormatter.getMessage("test.scenario.passed", scenario.getName()));
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
