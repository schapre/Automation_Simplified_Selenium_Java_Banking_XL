package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class DriverFactory {
    private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
    private static WebDriver webDriver;
    private static String apiBaseUrl;
    private static AppiumDriver mobileDriver;

    // Web UI Driver
    public static WebDriver initWebDriver() {
        if (webDriver == null) {
            logger.info("Initializing Web Driver (Chrome)");
            logger.debug(MessageFormatter.getDriverMessage("setup.chrome"));
            // Automatically manage ChromeDriver version
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver();
            webDriver.manage().window().maximize();
            logger.info("Web Driver initialized and window maximized");
            // Note: Removed automatic navigation to baseUrl to allow individual tests to
            // control navigation
        } else {
            logger.debug(MessageFormatter.getDriverMessage("already.initialized"));
        }
        return webDriver;
    }

    // API Client Initialization
    public static void initApiClient() {
        logger.debug(MessageFormatter.getDriverMessage("api.url.reading"));
        apiBaseUrl = ConfigReader.getProperty("api.baseUrl");
        RestAssured.baseURI = apiBaseUrl;
        logger.info("API Client initialized with base URL: {}", apiBaseUrl);
    }

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    // Mobile Driver (Appium)
    public static AppiumDriver initMobileDriver() {
        if (mobileDriver == null) {
            try {
                logger.info("Initializing Mobile Driver (Appium)");
                DesiredCapabilities caps = new DesiredCapabilities();
                String platformName = ConfigReader.getProperty("mobile.platformName");
                String deviceName = ConfigReader.getProperty("mobile.deviceName");
                logger.debug(MessageFormatter.getDriverMessage("mobile.platform", platformName, deviceName));
                caps.setCapability("platformName", platformName);
                caps.setCapability("deviceName", deviceName);
                caps.setCapability("automationName", ConfigReader.getProperty("mobile.automationName"));
                caps.setCapability("app", ConfigReader.getProperty("mobile.appPath"));

                mobileDriver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
                logger.info("Mobile Driver initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize mobile driver", e);
                throw new RuntimeException("Failed to initialize mobile driver", e);
            }
        }
        return mobileDriver;
    }

    // Quit drivers
    public static void quitDriver() {
        boolean anyDriverClosed = false;
        if (webDriver != null) {
            logger.info("Quitting Web Driver");
            webDriver.quit();
            webDriver = null;
            anyDriverClosed = true;
        }
        if (mobileDriver != null) {
            logger.info("Quitting Mobile Driver");
            mobileDriver.quit();
            mobileDriver = null;
            anyDriverClosed = true;
        }
        if (anyDriverClosed) {
            logger.info("All drivers closed successfully");
        } else {
            logger.debug("No active drivers to quit");
        }
    }

    // Getters
    public static WebDriver getWebDriver() {
        return webDriver;
    }

    public static AppiumDriver getMobileDriver() {
        return mobileDriver;
    }
}
