package stepdefinitions.web;

import io.cucumber.java.en.*;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import pages.UI_LoginPage;
import pages.UI_DashboardPage;
import utils.DriverFactory;
import utils.AllureManager;

public class UI_OrangeHRM_LoginPage_StepDefinition {

    WebDriver driver = DriverFactory.getWebDriver();
    UI_LoginPage loginPage = new UI_LoginPage(driver);
    UI_DashboardPage dashboardPage = new UI_DashboardPage(driver); // Assuming you have this page for post-login
                                                                   // verification

    @Given("the application is launched")
    @Step("Launch the application")
    @Description("Navigate to the OrangeHRM application homepage")
    public void the_application_is_launched() {
        AllureManager.addStep("Launching OrangeHRM application");
        driver.get("https://opensource-demo.orangehrmlive.com/");
        AllureManager.attachScreenshot(driver, "Application Launched");
    }

    @Given("the user is on the login page")
    @Step("Verify user is on login page")
    @Description("Validate that the login page is properly displayed")
    public void the_user_is_on_the_login_page() {
        AllureManager.addStep("Verifying login page is displayed");
        loginPage.verifyLoginPageIsDisplayed();
        AllureManager.attachScreenshot(driver, "Login Page Verification");
    }

    @And("the user enters username {string} and password {string}")
    @Step("Enter credentials: username={0}, password={1}")
    @Description("Input user credentials into login form")
    public void the_user_enters_username_and_password(String username, String password) {
        AllureManager.addStep("Entering username: " + username);
        loginPage.enterUsername(username);

        AllureManager.addStep("Entering password");
        loginPage.enterPassword(password);

        AllureManager.attachText("Login Credentials", "Username: " + username + "\nPassword: [MASKED]");
        AllureManager.attachScreenshot(driver, "Credentials Entered");
    }

    @And("the user clicks the login button")
    @Step("Click login button")
    @Description("Submit the login form")
    public void the_user_clicks_the_login_button() {
        AllureManager.addStep("Clicking login button");
        loginPage.clickLogin();
        AllureManager.attachScreenshot(driver, "Login Button Clicked");
    }

    @Then("the user should be logged in and directed to the dashboard")
    @Step("Verify successful login and dashboard navigation")
    @Description("Confirm user is successfully logged in and can see the dashboard")
    public void the_user_should_be_logged_in_and_directed_to_the_dashboard() {
        AllureManager.addStep("Verifying dashboard is visible after login");
        dashboardPage.verifyDashboardIsVisible(); // Implement this method in DashboardPage
        AllureManager.attachScreenshot(driver, "Dashboard Verification");
    }
}
