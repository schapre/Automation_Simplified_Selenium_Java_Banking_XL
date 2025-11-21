package runners;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import utils.DriverFactory;
import utils.PlatformSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CucumberOptions(features = {
                "src/test/resources/features/web",
                // "src/test/resources/features/mobile",
                "src/test/resources/features/api"
}, glue = {
                "hooks",
                "stepdefinitions.web",
                // "stepdefinitions.mobile",
                "stepdefinitions.api"
}, tags = "@WEB or @API", // Run Web tests only (API tests have UI issues)
                plugin = {
                                "pretty", // Console output
                                "html:target/cucumber-reports/cucumber-html-report.html", // HTML report
                                "json:target/cucumber-reports/cucumber.json", // JSON report
                                "junit:target/cucumber-reports/cucumber.xml", // JUnit XML report
                                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:", // For Extent
                                                                                                        // Reports
                                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // For Allure Reports
                }, monochrome = true)
public class TestRunner extends AbstractTestNGCucumberTests {
        private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

        @Parameters({ "configFile" })
        @BeforeClass
        public void setUp(String configFile) {
                logger.info("Initializing platform using config: {}", configFile);
                PlatformSelector.initializePlatform(configFile);
        }

        @AfterClass
        public void tearDown() {
                logger.info("Quitting drivers...");
                DriverFactory.quitDriver();
        }
}
