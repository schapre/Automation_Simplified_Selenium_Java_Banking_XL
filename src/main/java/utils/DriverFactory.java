package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class DriverFactory {
    private static WebDriver webDriver;
    private static String apiBaseUrl;
    private static AppiumDriver mobileDriver;

    // Web UI Driver
    public static WebDriver initWebDriver() {
        if (webDriver == null) {
            // Automatically manage ChromeDriver version
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver();
            webDriver.manage().window().maximize();
            // Note: Removed automatic navigation to baseUrl to allow individual tests to control navigation
        }
        return webDriver;
    }

    // API Client Initialization
    public static void initApiClient() {
        apiBaseUrl = ConfigReader.getProperty("api.baseUrl");
        RestAssured.baseURI = apiBaseUrl;
    }

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }
    
    // Mobile Driver (Appium)
    public static AppiumDriver initMobileDriver() {
        if (mobileDriver == null) {
            try {
                DesiredCapabilities caps = new DesiredCapabilities();
                caps.setCapability("platformName", ConfigReader.getProperty("mobile.platformName"));
                caps.setCapability("deviceName", ConfigReader.getProperty("mobile.deviceName"));
                caps.setCapability("automationName", ConfigReader.getProperty("mobile.automationName"));
                caps.setCapability("app", ConfigReader.getProperty("mobile.appPath"));

                mobileDriver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize mobile driver");
            }
        }
        return mobileDriver;
    }


   

    // Quit drivers
    public static void quitDriver() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
        if (mobileDriver != null) {
            mobileDriver.quit();
            mobileDriver = null;
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
