package utils;

import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;
import java.io.FileInputStream;
//import java.io.IOException;
import java.util.Properties;
//import utils.JiraUtil;

/**
 * TestNG Listener for Allure integration
 */
public class AllureTestNGListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();

        Allure.step("Starting test: " + testName + " in class: " + className);
        AllureManager.setEnvironmentInfo("Test Class", className);
        AllureManager.setEnvironmentInfo("Test Method", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Allure.step("Test passed: " + testName);
        AllureManager.attachText("Test Status", "PASSED: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();

        Allure.step("Test failed: " + testName);

        if (throwable != null) {
            AllureManager.attachText("Error Details",
                    "Test: " + testName + "\n" +
                            "Error: " + throwable.getMessage() + "\n" +
                            "Stack Trace: " + getStackTrace(throwable));
        }

        // Try to capture screenshot if WebDriver is available
        try {
            if (DriverFactory.getWebDriver() != null) {
                AllureManager.attachScreenshot(DriverFactory.getWebDriver(), "Failure Screenshot");
            }
        } catch (Exception e) {
            // Ignore screenshot errors
        }

        // JIRA Integration: Log bug for failed test
        try (FileInputStream fis = new FileInputStream("src/main/resources/jira.properties")) {
            Properties props = new Properties();
            props.load(fis);
            String jiraUrl = props.getProperty("jira.url");
            String jiraUser = props.getProperty("jira.username");
            String jiraPass = props.getProperty("jira.password");
            String jiraProject = props.getProperty("jira.project");
            JiraUtil jiraUtil = new JiraUtil(jiraUrl, jiraUser, jiraPass, jiraProject);
            String summary = "[Automation Bug] " + testName + " failed";
            String description = "Test: " + testName + "\nError: "
                    + (throwable != null ? throwable.getMessage() : "Unknown error") + "\nStack Trace:\n"
                    + (throwable != null ? getStackTrace(throwable) : "");
            String issueKey = jiraUtil.createIssue(summary, description);
            if (issueKey != null) {
                Allure.step("JIRA bug logged: " + issueKey);
            } else {
                Allure.step("Failed to log bug in JIRA");
            }
            jiraUtil.close();
        } catch (Exception e) {
            Allure.step("JIRA logging error: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        Allure.step("Test skipped: " + testName);
        AllureManager.attachText("Test Status", "SKIPPED: " + testName);
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}