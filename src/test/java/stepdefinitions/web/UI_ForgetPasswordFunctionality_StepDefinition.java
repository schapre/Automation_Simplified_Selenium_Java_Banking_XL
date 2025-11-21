package stepdefinitions.web;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;
import pages.UI_ForgetPasswordTestPage;
import utils.DriverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UI_ForgetPasswordFunctionality_StepDefinition {
    private static final Logger logger = LoggerFactory.getLogger(UI_ForgetPasswordFunctionality_StepDefinition.class);

    private UI_ForgetPasswordTestPage forgetPasswordPage;

    public UI_ForgetPasswordFunctionality_StepDefinition() {
        this.forgetPasswordPage = new UI_ForgetPasswordTestPage(DriverFactory.getWebDriver());
    }

    // Background Step
    @Given("I am on the forget password page")
    public void i_am_on_the_forget_password_page() {
        logger.debug("Step: Navigating to forget password page");
        forgetPasswordPage.navigateToForgetPasswordPage();
        Assert.assertTrue(forgetPasswordPage.isEmailFieldDisplayed(),
                "Forget password page should be displayed with email field");
        logger.info("Navigated to forget password page");
    }

    // Email Entry Steps
    @When("I leave the email address field blank")
    public void i_leave_the_email_address_field_blank() {
        forgetPasswordPage.enterEmail("");
    }

    @When("I enter wrong email address {string}")
    public void i_enter_wrong_email_address(String wrongEmail) {
        forgetPasswordPage.enterEmail(wrongEmail);
    }

    @When("I enter valid email address {string}")
    public void i_enter_valid_email_address(String validEmail) {
        logger.debug("Step: Entering valid email - {}", validEmail);
        forgetPasswordPage.enterEmail(validEmail);
        logger.info("Entered valid email: {}", validEmail);
    }

    @And("I click on 'Continue' button")
    public void i_click_on_continue_button() {
        logger.debug("Step: Clicking Continue button");
        forgetPasswordPage.clickContinueButton();
        logger.info("Clicked Continue button");
    }

    // Security Code Steps
    @And("I leave the secret code field blank")
    public void i_leave_the_secret_code_field_blank() {
        Assert.assertTrue(forgetPasswordPage.isSecurityCodeFieldDisplayed(),
                "Security code field should be displayed");
        forgetPasswordPage.enterSecurityCode("");
    }

    @And("I enter wrong secret code {string}")
    public void i_enter_wrong_secret_code(String wrongCode) {
        Assert.assertTrue(forgetPasswordPage.isSecurityCodeFieldDisplayed(),
                "Security code field should be displayed");
        forgetPasswordPage.enterSecurityCode(wrongCode);
    }

    @And("I enter correct secret code {string}")
    public void i_enter_correct_secret_code(String correctCode) {
        Assert.assertTrue(forgetPasswordPage.isSecurityCodeFieldDisplayed(),
                "Security code field should be displayed");
        forgetPasswordPage.enterSecurityCode(correctCode);
    }

    @And("I click on 'Verify Code' button")
    public void i_click_on_verify_code_button() {
        forgetPasswordPage.clickVerifyCodeButton();
    }

    // Password Reset Steps
    @And("I leave the current password field blank")
    public void i_leave_the_current_password_field_blank() {
        Assert.assertTrue(forgetPasswordPage.isPasswordResetFormDisplayed(),
                "Password reset form should be displayed");
        forgetPasswordPage.enterCurrentPassword("");
    }

    @And("I enter current password {string}")
    public void i_enter_current_password(String currentPassword) {
        Assert.assertTrue(forgetPasswordPage.isPasswordResetFormDisplayed(),
                "Password reset form should be displayed");
        forgetPasswordPage.enterCurrentPassword(currentPassword);
    }

    @And("I leave the new password field blank")
    public void i_leave_the_new_password_field_blank() {
        forgetPasswordPage.enterNewPassword("");
    }

    @And("I enter new password {string}")
    public void i_enter_new_password(String newPassword) {
        forgetPasswordPage.enterNewPassword(newPassword);
    }

    @And("I enter weak new password {string}")
    public void i_enter_weak_new_password(String weakPassword) {
        forgetPasswordPage.enterNewPassword(weakPassword);
    }

    @And("I enter partial criteria new password {string}")
    public void i_enter_partial_criteria_new_password(String partialPassword) {
        forgetPasswordPage.enterNewPassword(partialPassword);
    }

    @And("I enter valid new password {string}")
    public void i_enter_valid_new_password(String validPassword) {
        forgetPasswordPage.enterNewPassword(validPassword);
    }

    @And("I leave the confirm password field blank")
    public void i_leave_the_confirm_password_field_blank() {
        forgetPasswordPage.enterConfirmPassword("");
    }

    @And("I enter confirm password {string}")
    public void i_enter_confirm_password(String confirmPassword) {
        forgetPasswordPage.enterConfirmPassword(confirmPassword);
    }

    @And("I enter different confirm password {string}")
    public void i_enter_different_confirm_password(String differentPassword) {
        forgetPasswordPage.enterConfirmPassword(differentPassword);
    }

    @And("I enter same weak confirm password {string}")
    public void i_enter_same_weak_confirm_password(String weakPassword) {
        forgetPasswordPage.enterConfirmPassword(weakPassword);
    }

    @And("I enter same partial criteria confirm password {string}")
    public void i_enter_same_partial_criteria_confirm_password(String partialPassword) {
        forgetPasswordPage.enterConfirmPassword(partialPassword);
    }

    @And("I enter same valid confirm password {string}")
    public void i_enter_same_valid_confirm_password(String validPassword) {
        forgetPasswordPage.enterConfirmPassword(validPassword);
    }

    @And("I click on 'Reset Password' button")
    public void i_click_on_reset_password_button() {
        forgetPasswordPage.clickResetPasswordButton();
    }

    // Validation Steps - Error Messages
    @Then("I should see error message {string}")
    public void i_should_see_error_message(String expectedErrorMessage) {
        boolean errorFound = false;

        switch (expectedErrorMessage) {
            case "Email is required":
                errorFound = forgetPasswordPage.isEmailRequiredErrorDisplayed();
                break;
            case "Email not found in our records":
                errorFound = forgetPasswordPage.isEmailNotFoundErrorDisplayed();
                break;
            case "Secret code is required":
                errorFound = forgetPasswordPage.isCodeRequiredErrorDisplayed();
                break;
            case "Invalid secret code":
                errorFound = forgetPasswordPage.isInvalidCodeErrorDisplayed();
                break;
            case "Current password is required":
                errorFound = forgetPasswordPage.isCurrentPasswordRequiredErrorDisplayed();
                break;
            case "New password is required":
                errorFound = forgetPasswordPage.isNewPasswordRequiredErrorDisplayed();
                break;
            case "Please confirm your new password":
                errorFound = forgetPasswordPage.isConfirmPasswordRequiredErrorDisplayed();
                break;
            case "New passwords do not match":
                errorFound = forgetPasswordPage.isPasswordMismatchErrorDisplayed();
                break;
            case "Password does not meet the requirements":
            case "Password does not meet requirements":
                errorFound = forgetPasswordPage.isPasswordRequirementsErrorDisplayed();
                break;
            default:
                Assert.fail("Unknown error message: " + expectedErrorMessage);
        }

        Assert.assertTrue(errorFound,
                "Expected error message '" + expectedErrorMessage
                        + "' (or equivalent like 'All fields are required') should be displayed but was not found");
    } // Validation Steps - Password Requirements

    @Then("I should see password requirements are displayed")
    public void i_should_see_password_requirements_are_displayed() {
        Assert.assertTrue(forgetPasswordPage.arePasswordRequirementsDisplayed(),
                "Password requirements should be displayed on the page");
    }

    @And("the password requirements should show current status")
    public void the_password_requirements_should_show_current_status() {
        // Verify that password requirements are visible and contain expected text
        String req8Chars = forgetPasswordPage.getPasswordRequirement8Characters();
        String reqUpper = forgetPasswordPage.getPasswordRequirementUppercase();
        String reqLower = forgetPasswordPage.getPasswordRequirementLowercase();
        String reqNumber = forgetPasswordPage.getPasswordRequirementNumber();
        String reqSpecial = forgetPasswordPage.getPasswordRequirementSpecial();

        Assert.assertFalse(req8Chars.isEmpty(), "8 characters requirement should be displayed");
        Assert.assertFalse(reqUpper.isEmpty(), "Uppercase requirement should be displayed");
        Assert.assertFalse(reqLower.isEmpty(), "Lowercase requirement should be displayed");
        Assert.assertFalse(reqNumber.isEmpty(), "Number requirement should be displayed");
        Assert.assertFalse(reqSpecial.isEmpty(), "Special character requirement should be displayed");

        logger.info("Password Requirements Status:");
        logger.info("8 Characters: {}", req8Chars);
        logger.info("Uppercase: {}", reqUpper);
        logger.info("Lowercase: {}", reqLower);
        logger.info("Number: {}", reqNumber);
        logger.info("Special Character: {}", reqSpecial);
    }

    // Validation Steps - Success Messages
    @Then("I should see success message {string}")
    public void i_should_see_success_message(String expectedSuccessMessage) {
        Assert.assertTrue(forgetPasswordPage.isSuccessPageDisplayed(),
                "Success page should be displayed");

        String actualSuccessMessage = forgetPasswordPage.getSuccessMessage();
        Assert.assertTrue(actualSuccessMessage.contains("password has been updated") ||
                actualSuccessMessage.contains("successfully"),
                "Success message should contain expected text. Actual: " + actualSuccessMessage);
    }

    @And("I should see {string} button displayed")
    public void i_should_see_button_displayed(String buttonText) {
        if (buttonText.equals("Return to Login")) {
            Assert.assertTrue(forgetPasswordPage.isReturnToLoginButtonDisplayed(),
                    "Return to Login button should be displayed");
        } else {
            Assert.fail("Unknown button text: " + buttonText);
        }
    }

    // Additional helper steps for navigation
    @When("I click on 'Return to Login' button")
    public void i_click_on_return_to_login_button() {
        forgetPasswordPage.clickReturnToLoginButton();
    }

    @When("I click on 'Back to Login' link")
    public void i_click_on_back_to_login_link() {
        forgetPasswordPage.clickBackToLogin();
    }
}