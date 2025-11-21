package stepdefinitions.web;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import pages.UI_LoginTestPage;
import utils.DriverFactory;
import utils.MessageFormatter;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UI_LoginFunctionality_StepDefinition {

    private static final Logger logger = LoggerFactory.getLogger(UI_LoginFunctionality_StepDefinition.class);
    private WebDriver driver;
    private UI_LoginTestPage loginPage;

    public UI_LoginFunctionality_StepDefinition() {
        this.driver = DriverFactory.getWebDriver();
        this.loginPage = new UI_LoginTestPage(driver);
    }

    @Given("I navigate to the login page")
    public void i_navigate_to_the_login_page() {
        logger.debug(MessageFormatter.getStepMessage("navigating.login"));
        loginPage.navigateToLoginPage();
        logger.info(MessageFormatter.getMessage("nav.login.page"));
    }

    // TC_Basic_01 Step Definitions
    @When("I enter valid email id {string} and password {string}")
    public void i_enter_valid_email_id_and_password(String email, String password) {
        logger.debug(MessageFormatter.getStepMessage("entering.credentials", email));
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        logger.info(MessageFormatter.getMessage("auth.credentials.entered", email));
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        logger.debug(MessageFormatter.getStepMessage("clicking.button", "login"));
        loginPage.clickLoginButton();
        logger.info(MessageFormatter.getMessage("auth.login.button.clicked"));
    }

    @Then("I should see successful message {string}")
    public void i_should_see_successful_message(String expectedMessage) {
        logger.debug(MessageFormatter.getStepMessage("verifying.message", expectedMessage));
        Assert.assertTrue(loginPage.isSuccessMessageDisplayed(),
                MessageFormatter.getMessage("assert.success.not.displayed"));
        String actualMessage = loginPage.getSuccessMessageText();
        Assert.assertTrue(actualMessage.contains(expectedMessage),
                MessageFormatter.formatAssertionMessage("assert.expected.vs.actual", expectedMessage, actualMessage));
        logger.info(MessageFormatter.getValidationMessage("success.verified", actualMessage));
    }

    @And("I should see {string} button")
    public void i_should_see_button(String buttonText) {
        if (buttonText.equals("Back to Login")) {
            Assert.assertTrue(loginPage.isBackToLoginButtonDisplayed(),
                    MessageFormatter.getMessage("assert.back.button.not.displayed"));
            logger.info(MessageFormatter.getValidationMessage("button.displayed", buttonText));
        }
    }

    // TC_Basic_02 Step Definitions
    @When("I enter invalid email id {string} or password {string}")
    public void i_enter_invalid_email_id_or_password(String email, String password) {
        logger.debug(MessageFormatter.getStepMessage("entering.invalid.credentials", email));
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        logger.info(MessageFormatter.getMessage("auth.invalid.credentials.entered", email));
    }

    @Then("error message {string} should be displayed")
    public void error_message_should_be_displayed(String expectedErrorMessage) {
        if (expectedErrorMessage.contains("Invalid email id and password")) {
            Assert.assertTrue(loginPage.isInvalidCredentialsErrorDisplayed(),
                    MessageFormatter.getMessage("error.invalid.credentials"));
            String actualMessage = loginPage.getInvalidCredentialsErrorText();
            Assert.assertTrue(actualMessage.contains("Invalid email id and password"),
                    MessageFormatter.formatAssertionMessage("assert.expected.error.vs.actual", expectedErrorMessage,
                            actualMessage));
            logger.info(MessageFormatter.getValidationMessage("invalid.credentials", actualMessage));

        } else if (expectedErrorMessage.contains("Password is required")) {
            Assert.assertTrue(loginPage.isPasswordRequiredErrorDisplayed(),
                    MessageFormatter.getMessage("error.password.required"));
            String actualMessage = loginPage.getPasswordRequiredErrorText();
            Assert.assertTrue(actualMessage.contains("Password is required"),
                    MessageFormatter.formatAssertionMessage("assert.expected.error.vs.actual", expectedErrorMessage,
                            actualMessage));
            logger.info(MessageFormatter.getValidationMessage("password.required", actualMessage));

        } else if (expectedErrorMessage.contains("Please include an '@'")) {
            String browserMessage = loginPage.getBrowserValidationMessage();
            Assert.assertTrue(browserMessage.contains("Please include an '@'"),
                    MessageFormatter.getMessage("assert.browser.validation.not.found", browserMessage));
            logger.info(MessageFormatter.getValidationMessage("browser.validation", browserMessage));
        }
    }

    // TC_Basic_03 Step Definitions
    @Then("I should see Email Address field is displayed")
    public void i_should_see_email_address_field_is_displayed() {
        Assert.assertTrue(loginPage.isEmailFieldDisplayed(),
                "Email Address field is not displayed");
        logger.info("Email Address field is displayed");
    }

    @And("I should see Password field is displayed")
    public void i_should_see_password_field_is_displayed() {
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(),
                "Password field is not displayed");
        logger.info("Password field is displayed");
    }

    @And("I should see Login button is displayed")
    public void i_should_see_login_button_is_displayed() {
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button is not displayed");
        logger.info("Login button is displayed");
    }

    @And("I should see Forget Password Link is displayed")
    public void i_should_see_forget_password_link_is_displayed() {
        Assert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(),
                "Forget Password Link is not displayed");
        logger.info("Forget Password Link is displayed");
    }

    @And("I should see Register now Link is displayed")
    public void i_should_see_register_now_link_is_displayed() {
        Assert.assertTrue(loginPage.isRegisterNowLinkDisplayed(),
                "Register now Link is not displayed");
        logger.info("Register now Link is displayed");
    }

    // TC_Basic_04 Step Definitions
    @When("I keep username and password fields empty")
    public void i_keep_username_and_password_fields_empty() {
        loginPage.clearEmailField();
        loginPage.clearPasswordField();
        logger.info("Kept username and password fields empty");
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLoginButton();
        logger.info("Clicked the login button");
    }

    @Then("Login button should be enabled")
    public void login_button_should_be_enabled() {
        Assert.assertTrue(loginPage.isLoginButtonEnabled(),
                "Login button is not enabled");
        logger.info("Login button is enabled");
    }

    @And("I should get error message {string}")
    public void i_should_get_error_message(String expectedErrorMessage) {
        if (expectedErrorMessage.contains("Email and Password are required")) {
            Assert.assertTrue(loginPage.isEmailPasswordRequiredErrorDisplayed(),
                    "Email and Password required error message is not displayed");
            String actualMessage = loginPage.getEmailPasswordRequiredErrorText();
            Assert.assertTrue(actualMessage.contains("Email and Password are required"),
                    "Expected error: " + expectedErrorMessage + " but found: " + actualMessage);
            logger.info("Email and Password required error verified: {}", actualMessage);
        }
    }

    // TC_Basic_05 Step Definitions
    @When("I enter password {string} in the password field")
    public void i_enter_password_in_the_password_field(String password) {
        loginPage.enterPassword(password);
        logger.info("Entered password in the password field");
    }

    @Then("Password should be masked with dots")
    public void password_should_be_masked_with_dots() {
        Assert.assertTrue(loginPage.isPasswordMasked(),
                "Password field is not masked");
        logger.info("Password field is properly masked");
    }

    // TC_Basic_06 Step Definitions
    @When("I enter valid username {string} and keep password field empty")
    public void i_enter_valid_username_and_keep_password_field_empty(String username) {
        loginPage.enterEmail(username);
        loginPage.clearPasswordField();
        logger.info("Entered username: {} - password field kept empty", username);
    }

    // TC_Basic_07 Step Definitions
    @When("I enter invalid format username {string}")
    public void i_enter_invalid_format_username(String invalidUsername) {
        loginPage.enterEmail(invalidUsername);
        logger.info("Entered invalid format username: {}", invalidUsername);
    }
}