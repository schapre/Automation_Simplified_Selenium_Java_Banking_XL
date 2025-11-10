package stepdefinitions.web;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import pages.UI_LoginTestPage;
import utils.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class UI_LoginFunctionality_StepDefinition {
    
    private WebDriver driver;
    private UI_LoginTestPage loginPage;
    
    public UI_LoginFunctionality_StepDefinition() {
        this.driver = DriverFactory.getWebDriver();
        this.loginPage = new UI_LoginTestPage(driver);
    }
    
    @Given("I navigate to the login page")
    public void i_navigate_to_the_login_page() {
        loginPage.navigateToLoginPage();
        System.out.println("Navigated to login page");
    }
    
    // TC_Basic_01 Step Definitions
    @When("I enter valid email id {string} and password {string}")
    public void i_enter_valid_email_id_and_password(String email, String password) {
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        System.out.println("Entered valid email: " + email + " and password");
    }
    
    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.clickLoginButton();
        System.out.println("Clicked on login button");
    }
    
    @Then("I should see successful message {string}")
    public void i_should_see_successful_message(String expectedMessage) {
        Assert.assertTrue(loginPage.isSuccessMessageDisplayed(), 
            "Success message is not displayed");
        String actualMessage = loginPage.getSuccessMessageText();
        Assert.assertTrue(actualMessage.contains(expectedMessage), 
            "Expected message: " + expectedMessage + " but found: " + actualMessage);
        System.out.println("Success message verified: " + actualMessage);
    }
    
    @And("I should see {string} button")
    public void i_should_see_button(String buttonText) {
        if (buttonText.equals("Back to Login")) {
            Assert.assertTrue(loginPage.isBackToLoginButtonDisplayed(), 
                "Back to Login button is not displayed");
            System.out.println("Back to Login button is displayed");
        }
    }
    
    // TC_Basic_02 Step Definitions
    @When("I enter invalid email id {string} or password {string}")
    public void i_enter_invalid_email_id_or_password(String email, String password) {
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        System.out.println("Entered invalid email: " + email + " and password");
    }
    
    @Then("error message {string} should be displayed")
    public void error_message_should_be_displayed(String expectedErrorMessage) {
        if (expectedErrorMessage.contains("Invalid email id and password")) {
            Assert.assertTrue(loginPage.isInvalidCredentialsErrorDisplayed(), 
                "Invalid credentials error message is not displayed");
            String actualMessage = loginPage.getInvalidCredentialsErrorText();
            Assert.assertTrue(actualMessage.contains("Invalid email id and password"), 
                "Expected error: " + expectedErrorMessage + " but found: " + actualMessage);
            System.out.println("Invalid credentials error verified: " + actualMessage);
            
        } else if (expectedErrorMessage.contains("Password is required")) {
            Assert.assertTrue(loginPage.isPasswordRequiredErrorDisplayed(), 
                "Password required error message is not displayed");
            String actualMessage = loginPage.getPasswordRequiredErrorText();
            Assert.assertTrue(actualMessage.contains("Password is required"), 
                "Expected error: " + expectedErrorMessage + " but found: " + actualMessage);
            System.out.println("Password required error verified: " + actualMessage);
            
        } else if (expectedErrorMessage.contains("Please include an '@'")) {
            String browserMessage = loginPage.getBrowserValidationMessage();
            Assert.assertTrue(browserMessage.contains("Please include an '@'"), 
                "Expected browser validation message not found. Actual: " + browserMessage);
            System.out.println("Browser validation error verified: " + browserMessage);
        }
    }
    
    // TC_Basic_03 Step Definitions
    @Then("I should see Email Address field is displayed")
    public void i_should_see_email_address_field_is_displayed() {
        Assert.assertTrue(loginPage.isEmailFieldDisplayed(), 
            "Email Address field is not displayed");
        System.out.println("Email Address field is displayed");
    }
    
    @And("I should see Password field is displayed")
    public void i_should_see_password_field_is_displayed() {
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), 
            "Password field is not displayed");
        System.out.println("Password field is displayed");
    }
    
    @And("I should see Login button is displayed")
    public void i_should_see_login_button_is_displayed() {
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), 
            "Login button is not displayed");
        System.out.println("Login button is displayed");
    }
    
    @And("I should see Forget Password Link is displayed")
    public void i_should_see_forget_password_link_is_displayed() {
        Assert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(), 
            "Forget Password Link is not displayed");
        System.out.println("Forget Password Link is displayed");
    }
    
    @And("I should see Register now Link is displayed")
    public void i_should_see_register_now_link_is_displayed() {
        Assert.assertTrue(loginPage.isRegisterNowLinkDisplayed(), 
            "Register now Link is not displayed");
        System.out.println("Register now Link is displayed");
    }
    
    // TC_Basic_04 Step Definitions
    @When("I keep username and password fields empty")
    public void i_keep_username_and_password_fields_empty() {
        loginPage.clearEmailField();
        loginPage.clearPasswordField();
        System.out.println("Kept username and password fields empty");
    }
    
    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLoginButton();
        System.out.println("Clicked the login button");
    }
    
    @Then("Login button should be enabled")
    public void login_button_should_be_enabled() {
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), 
            "Login button is not enabled");
        System.out.println("Login button is enabled");
    }
    
    @And("I should get error message {string}")
    public void i_should_get_error_message(String expectedErrorMessage) {
        if (expectedErrorMessage.contains("Email and Password are required")) {
            Assert.assertTrue(loginPage.isEmailPasswordRequiredErrorDisplayed(), 
                "Email and Password required error message is not displayed");
            String actualMessage = loginPage.getEmailPasswordRequiredErrorText();
            Assert.assertTrue(actualMessage.contains("Email and Password are required"), 
                "Expected error: " + expectedErrorMessage + " but found: " + actualMessage);
            System.out.println("Email and Password required error verified: " + actualMessage);
        }
    }
    
    // TC_Basic_05 Step Definitions
    @When("I enter password {string} in the password field")
    public void i_enter_password_in_the_password_field(String password) {
        loginPage.enterPassword(password);
        System.out.println("Entered password in the password field");
    }
    
    @Then("Password should be masked with dots")
    public void password_should_be_masked_with_dots() {
        Assert.assertTrue(loginPage.isPasswordMasked(), 
            "Password field is not masked");
        System.out.println("Password field is properly masked");
    }
    
    // TC_Basic_06 Step Definitions
    @When("I enter valid username {string} and keep password field empty")
    public void i_enter_valid_username_and_keep_password_field_empty(String username) {
        loginPage.enterEmail(username);
        loginPage.clearPasswordField();
        System.out.println("Entered username: " + username + " and kept password field empty");
    }
    
    // TC_Basic_07 Step Definitions
    @When("I enter invalid format username {string}")
    public void i_enter_invalid_format_username(String invalidUsername) {
        loginPage.enterEmail(invalidUsername);
        System.out.println("Entered invalid format username: " + invalidUsername);
    }
}