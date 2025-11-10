package stepdefinitions.web;

import io.cucumber.java.en.*;
import org.testng.Assert;
import pages.UI_RegistrationTestPage;
import pages.UI_LoginTestPage;
import utils.DriverFactory;

public class UI_RegistrationFunctionality_StepDefinition {
    
    UI_RegistrationTestPage registrationPage;
    UI_LoginTestPage loginPage;
    
    @Given("User navigates to TesterBud registration page")
    public void user_navigates_to_testerbud_registration_page() {
        registrationPage = new UI_RegistrationTestPage(DriverFactory.getWebDriver());
        registrationPage.navigateToRegistrationPage();
    }
    
    @Given("User is on the registration page")
    public void user_is_on_the_registration_page() {
        // Verify all registration form elements are visible
        Assert.assertTrue(registrationPage.isEmailFieldDisplayed(), "Email field is not displayed");
        Assert.assertTrue(registrationPage.isPasswordFieldDisplayed(), "Password field is not displayed");
        Assert.assertTrue(registrationPage.isConfirmPasswordFieldDisplayed(), "Confirm password field is not displayed");
        Assert.assertTrue(registrationPage.isRegisterButtonDisplayed(), "Register button is not displayed");
        Assert.assertTrue(registrationPage.isSignInLinkDisplayed(), "Sign in link is not displayed");
    }
    
    @When("User enters valid email {string}")
    public void user_enters_valid_email(String email) {
        registrationPage.enterEmail(email);
    }
    
    @When("User enters valid password {string}")
    public void user_enters_valid_password(String password) {
        registrationPage.enterPassword(password);
    }
    
    @When("User enters confirm password {string}")
    public void user_enters_confirm_password(String confirmPassword) {
        registrationPage.enterConfirmPassword(confirmPassword);
    }
    
    @When("User clicks on Register button")
    public void user_clicks_on_register_button() {
        registrationPage.clickRegisterButton();
        // Wait a moment for any processing or page transition
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @When("User leaves email field empty")
    public void user_leaves_email_field_empty() {
        registrationPage.clearEmailField();
    }
    
    @When("User leaves password field empty")
    public void user_leaves_password_field_empty() {
        registrationPage.clearPasswordField();
    }
    
    @When("User leaves confirm password field empty")
    public void user_leaves_confirm_password_field_empty() {
        registrationPage.clearConfirmPasswordField();
    }
    
    @When("User enters invalid email {string}")
    public void user_enters_invalid_email(String invalidEmail) {
        registrationPage.enterEmail(invalidEmail);
    }
    
    @When("User enters password {string}")
    public void user_enters_password(String password) {
        registrationPage.enterPassword(password);
    }
    
    @When("User enters weak password {string}")
    public void user_enters_weak_password(String weakPassword) {
        registrationPage.enterPassword(weakPassword);
    }
    
    @When("User enters strong password {string}")
    public void user_enters_strong_password(String strongPassword) {
        registrationPage.enterPassword(strongPassword);
    }
    
    @When("User clicks on {string} link")
    public void user_clicks_on_link(String linkText) {
        if (linkText.equalsIgnoreCase("Sign in")) {
            registrationPage.clickSignInLink();
            // Wait for page transition
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Then("User should see registration success message")
    public void user_should_see_registration_success_message() {
        Assert.assertTrue(registrationPage.isSuccessMessageDisplayed(), 
            "Registration success message is not displayed");
        
        String successMessage = registrationPage.getSuccessMessageText();
        Assert.assertTrue(successMessage.toLowerCase().contains("success") || 
                         successMessage.toLowerCase().contains("registration") ||
                         successMessage.toLowerCase().contains("account") ||
                         successMessage.toLowerCase().contains("created"),
            "Success message does not contain expected text. Actual message: " + successMessage);
    }
    
    @Then("User should see {string} button")
    public void user_should_see_button(String buttonText) {
        if (buttonText.equalsIgnoreCase("Go to Login")) {
            Assert.assertTrue(registrationPage.isGoToLoginButtonDisplayed(), 
                "Go to Login button is not displayed");
        }
    }
    
    @Then("User should be redirected to success page or login page")
    public void user_should_be_redirected_to_success_page_or_login_page() {
        String currentUrl = DriverFactory.getWebDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("success") || 
                         currentUrl.contains("login") || 
                         registrationPage.isSuccessMessageDisplayed(),
            "User is not redirected to success or login page. Current URL: " + currentUrl);
    }
    
    @Then("User should see {string} error message")
    public void user_should_see_error_message(String expectedErrorMessage) {
        boolean errorDisplayed = false;
        String actualErrorMessage = "";
        
        switch (expectedErrorMessage.toLowerCase()) {
            case "email is required":
                errorDisplayed = registrationPage.isEmailRequiredErrorDisplayed();
                actualErrorMessage = registrationPage.getEmailRequiredErrorText();
                break;
            case "password is required":
                errorDisplayed = registrationPage.isPasswordRequiredErrorDisplayed();
                actualErrorMessage = registrationPage.getPasswordRequiredErrorText();
                break;
            case "confirm password is required":
                errorDisplayed = registrationPage.isConfirmPasswordRequiredErrorDisplayed();
                actualErrorMessage = registrationPage.getConfirmPasswordRequiredErrorText();
                break;
            case "passwords do not match":
                errorDisplayed = registrationPage.isPasswordMismatchErrorDisplayed();
                actualErrorMessage = registrationPage.getPasswordMismatchErrorText();
                break;
            case "password does not meet requirements":
                errorDisplayed = registrationPage.isWeakPasswordErrorDisplayed();
                actualErrorMessage = registrationPage.getWeakPasswordErrorText();
                break;
        }
        
        // Also check browser validation message for HTML5 validation
        if (!errorDisplayed) {
            String browserValidationMessage = registrationPage.getBrowserValidationMessage();
            if (!browserValidationMessage.isEmpty()) {
                errorDisplayed = true;
                actualErrorMessage = browserValidationMessage;
            }
        }
        
        Assert.assertTrue(errorDisplayed, 
            "Expected error message '" + expectedErrorMessage + "' is not displayed. Actual message: " + actualErrorMessage);
    }
    
    @Then("User should see invalid email format error message")
    public void user_should_see_invalid_email_format_error_message() {
        boolean errorDisplayed = registrationPage.isEmailFormatErrorDisplayed();
        String actualErrorMessage = registrationPage.getEmailFormatErrorText();
        
        // Also check browser validation message for HTML5 validation
        if (!errorDisplayed) {
            String browserValidationMessage = registrationPage.getBrowserValidationMessage();
            if (!browserValidationMessage.isEmpty()) {
                errorDisplayed = true;
                actualErrorMessage = browserValidationMessage;
            }
        }
        
        Assert.assertTrue(errorDisplayed, 
            "Invalid email format error message is not displayed. Actual message: " + actualErrorMessage);
    }
    
    @Then("User should remain on the registration page")
    public void user_should_remain_on_the_registration_page() {
        String currentUrl = DriverFactory.getWebDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("register"), 
            "User is not on the registration page. Current URL: " + currentUrl);
    }
    
    @Then("User should see password strength requirements")
    public void user_should_see_password_strength_requirements() {
        Assert.assertTrue(registrationPage.arePasswordStrengthIndicatorsVisible(), 
            "Password strength requirements are not visible");
    }
    
    @Then("Password strength indicators should be visible")
    public void password_strength_indicators_should_be_visible() {
        Assert.assertTrue(registrationPage.arePasswordStrengthIndicatorsVisible(), 
            "Password strength indicators are not visible");
    }
    

    
    @Then("User should be redirected to login page")
    public void user_should_be_redirected_to_login_page() {
        // Wait for page transition
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean isOnLoginPage = registrationPage.isOnLoginPage();
        String currentUrl = DriverFactory.getWebDriver().getCurrentUrl();
        
        Assert.assertTrue(isOnLoginPage, 
            "User is not redirected to login page. Current URL: " + currentUrl);
    }
    
    @Then("User should see login form with email and password fields")
    public void user_should_see_login_form_with_email_and_password_fields() {
        loginPage = new UI_LoginTestPage(DriverFactory.getWebDriver());
        
        Assert.assertTrue(loginPage.isEmailFieldDisplayed(), 
            "Email field is not displayed on login page");
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(), 
            "Password field is not displayed on login page");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(), 
            "Login button is not displayed on login page");
    }
    
    @Then("Login page URL should contain {string}")
    public void login_page_url_should_contain(String expectedUrlPart) {
        String currentUrl = DriverFactory.getWebDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains(expectedUrlPart), 
            "Login page URL does not contain '" + expectedUrlPart + "'. Current URL: " + currentUrl);
    }
}