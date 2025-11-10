package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.List;

public class UI_ForgetPasswordTestPage {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // Step 1: Email Entry Elements
    @FindBy(xpath = "//input[@placeholder='Enter your registered email' or contains(@name, 'email')]")
    private WebElement emailField;

    @FindBy(xpath = "//button[contains(text(), 'Continue') or @type='submit']")
    private WebElement continueButton;

    @FindBy(xpath = "//a[contains(text(), 'Back to Login') or contains(@href, 'login')]")
    private WebElement backToLoginLink;

    // Step 2: Security Code Elements
    @FindBy(xpath = "//input[@placeholder='Enter security code' or contains(@name, 'code')]")
    private WebElement securityCodeField;

    @FindBy(xpath = "//button[contains(text(), 'Verify Code') or contains(text(), 'Verify')]")
    private WebElement verifyCodeButton;

    // Step 3: Password Reset Elements
    @FindBy(xpath = "//input[@placeholder='Current password' or contains(@name, 'current')]")
    private WebElement currentPasswordField;

    @FindBy(xpath = "//input[@placeholder='New password' and not(contains(@placeholder, 'Confirm'))]")
    private WebElement newPasswordField;

    @FindBy(xpath = "//input[@placeholder='Confirm new password' or contains(@name, 'confirm')]")
    private WebElement confirmPasswordField;

    @FindBy(xpath = "//button[contains(text(), 'Reset Password') or contains(text(), 'Submit')]")
    private WebElement resetPasswordButton;

    // Password Requirements Elements
    @FindBy(xpath = "//div[contains(text(), 'Minimum 8 characters') or contains(text(), '8 characters')]")
    private WebElement requirement8Characters;

    @FindBy(xpath = "//div[contains(text(), 'uppercase') or contains(text(), 'Uppercase')]")
    private WebElement requirementUppercase;

    @FindBy(xpath = "//div[contains(text(), 'lowercase') or contains(text(), 'Lowercase')]")
    private WebElement requirementLowercase;

    @FindBy(xpath = "//div[contains(text(), 'number') or contains(text(), 'Number')]")
    private WebElement requirementNumber;

    @FindBy(xpath = "//div[contains(text(), 'special') or contains(text(), 'Special')]")
    private WebElement requirementSpecial;

    // Success Elements
    @FindBy(xpath = "//h2[contains(text(), 'Password Changed Successfully') or contains(text(), 'Success')]")
    private WebElement successHeading;

    @FindBy(xpath = "//p[contains(text(), 'password has been updated') or contains(text(), 'successfully')]")
    private WebElement successMessage;

    @FindBy(xpath = "//a[contains(text(), 'Return to Login') or contains(text(), 'Login')]")
    private WebElement returnToLoginButton;

    // Error Message Elements - Generic selectors for various error messages
    @FindBy(xpath = "//div[contains(@class, 'error') or contains(@class, 'alert-danger') or contains(@style, 'color: red')]")
    private List<WebElement> errorMessages;

    @FindBy(xpath = "//*[contains(text(), 'Email is required') or contains(text(), 'required')]")
    private WebElement emailRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Email not found') or contains(text(), 'not found')]")
    private WebElement emailNotFoundError;

    @FindBy(xpath = "//*[contains(text(), 'Secret code is required') or contains(text(), 'code is required')]")
    private WebElement codeRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'Invalid secret code') or contains(text(), 'Invalid code')]")
    private WebElement invalidCodeError;

    @FindBy(xpath = "//*[contains(text(), 'Current password is required') or contains(text(), 'current') and contains(text(), 'required')]")
    private WebElement currentPasswordRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'New password is required') or contains(text(), 'new') and contains(text(), 'required')]")
    private WebElement newPasswordRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'confirm your new password') or contains(text(), 'confirm') and contains(text(), 'required')]")
    private WebElement confirmPasswordRequiredError;

    @FindBy(xpath = "//*[contains(text(), 'passwords do not match') or contains(text(), 'not match')]")
    private WebElement passwordMismatchError;

    @FindBy(xpath = "//*[contains(text(), 'does not meet the requirements') or contains(text(), 'requirements')]")
    private WebElement passwordRequirementsError;

    public UI_ForgetPasswordTestPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.js = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }

    // Navigation Methods
    public void navigateToForgetPasswordPage() {
        try {
            driver.get("https://testerbud.com/forget-password");
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@placeholder='Enter your registered email' or contains(@name, 'email')]")));
        } catch (Exception e) {
            // Fallback: Navigate via login page
            driver.get("https://testerbud.com/practice-login-form");
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Forgot password')]")));
            clickElement(driver.findElement(By.xpath("//a[contains(text(), 'Forgot password')]")));
        }
    }

    // Step 1: Email Entry Methods
    public void enterEmail(String email) {
        waitAndClearAndType(emailField, email);
    }

    public void clickContinueButton() {
        clickElement(continueButton);
    }

    public void clickBackToLogin() {
        clickElement(backToLoginLink);
    }

    // Step 2: Security Code Methods
    public void enterSecurityCode(String code) {
        waitAndClearAndType(securityCodeField, code);
    }

    public void clickVerifyCodeButton() {
        clickElement(verifyCodeButton);
    }

    // Step 3: Password Reset Methods
    public void enterCurrentPassword(String password) {
        waitAndClearAndType(currentPasswordField, password);
    }

    public void enterNewPassword(String password) {
        waitAndClearAndType(newPasswordField, password);
    }

    public void enterConfirmPassword(String password) {
        waitAndClearAndType(confirmPasswordField, password);
    }

    public void clickResetPasswordButton() {
        clickElement(resetPasswordButton);
    }

    // Validation Methods
    public boolean isEmailFieldDisplayed() {
        return isElementDisplayed(emailField);
    }

    public boolean isSecurityCodeFieldDisplayed() {
        return isElementDisplayed(securityCodeField);
    }

    public boolean isPasswordResetFormDisplayed() {
        return isElementDisplayed(currentPasswordField) &&
                isElementDisplayed(newPasswordField) &&
                isElementDisplayed(confirmPasswordField);
    }

    public boolean isSuccessPageDisplayed() {
        return isElementDisplayed(successHeading) && isElementDisplayed(successMessage);
    }

    public boolean isReturnToLoginButtonDisplayed() {
        return isElementDisplayed(returnToLoginButton);
    }

    // Password Requirements Validation
    public boolean arePasswordRequirementsDisplayed() {
        return isElementDisplayed(requirement8Characters) &&
                isElementDisplayed(requirementUppercase) &&
                isElementDisplayed(requirementLowercase) &&
                isElementDisplayed(requirementNumber) &&
                isElementDisplayed(requirementSpecial);
    }

    public String getPasswordRequirement8Characters() {
        return getElementText(requirement8Characters);
    }

    public String getPasswordRequirementUppercase() {
        return getElementText(requirementUppercase);
    }

    public String getPasswordRequirementLowercase() {
        return getElementText(requirementLowercase);
    }

    public String getPasswordRequirementNumber() {
        return getElementText(requirementNumber);
    }

    public String getPasswordRequirementSpecial() {
        return getElementText(requirementSpecial);
    }

    // Error Message Methods
    public boolean isEmailRequiredErrorDisplayed() {
        return isErrorMessageDisplayed("Email is required") || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isEmailNotFoundErrorDisplayed() {
        return isErrorMessageDisplayed("Email not found in our records") || isErrorMessageDisplayed("Invalid email");
    }

    public boolean isCodeRequiredErrorDisplayed() {
        return isErrorMessageDisplayed("Secret code is required") || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isInvalidCodeErrorDisplayed() {
        return isErrorMessageDisplayed("Invalid secret code") || isErrorMessageDisplayed("Invalid code");
    }

    public boolean isCurrentPasswordRequiredErrorDisplayed() {
        return isErrorMessageDisplayed("Current password is required")
                || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isNewPasswordRequiredErrorDisplayed() {
        return isErrorMessageDisplayed("New password is required")
                || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isConfirmPasswordRequiredErrorDisplayed() {
        return isErrorMessageDisplayed("Please confirm your new password")
                || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isPasswordMismatchErrorDisplayed() {
        return isErrorMessageDisplayed("New passwords do not match")
                || isErrorMessageDisplayed("Passwords do not match")
                || isErrorMessageDisplayed("All fields are required");
    }

    public boolean isPasswordRequirementsErrorDisplayed() {
        return isErrorMessageDisplayed("Password does not meet requirements")
                || isErrorMessageDisplayed("Password does not meet the requirements")
                || isErrorMessageDisplayed("Invalid password") || isErrorMessageDisplayed("All fields are required");
    }

    public String getSuccessMessage() {
        return getElementText(successMessage);
    }

    public void clickReturnToLoginButton() {
        clickElement(returnToLoginButton);
    }

    // Helper Methods
    private boolean isErrorMessageDisplayed(String expectedMessage) {
        try {
            // Check for specific error elements first
            WebElement errorElement = driver.findElement(By.xpath("//*[contains(text(), '" + expectedMessage + "')]"));
            return errorElement.isDisplayed();
        } catch (Exception e) {
            // Fallback: Check in all error message containers
            for (WebElement errorMsg : errorMessages) {
                if (errorMsg.isDisplayed() && errorMsg.getText().contains(expectedMessage)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void waitAndClearAndType(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            // Fallback with JavaScript
            js.executeScript("arguments[0].value = arguments[1];", element, text);
        }
    }

    private void clickElement(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            // Fallback with JavaScript click
            js.executeScript("arguments[0].click();", element);
        }
    }

    private boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private String getElementText(WebElement element) {
        try {
            return element.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // Complete Forget Password Workflow Methods
    public void completeEmailStep(String email) {
        enterEmail(email);
        clickContinueButton();
    }

    public void completeSecurityCodeStep(String code) {
        enterSecurityCode(code);
        clickVerifyCodeButton();
    }

    public void completePasswordResetStep(String currentPassword, String newPassword, String confirmPassword) {
        enterCurrentPassword(currentPassword);
        enterNewPassword(newPassword);
        enterConfirmPassword(confirmPassword);
        clickResetPasswordButton();
    }

    public void performCompleteForgetPasswordWorkflow(String email, String securityCode,
            String currentPassword, String newPassword) {
        navigateToForgetPasswordPage();
        completeEmailStep(email);
        completeSecurityCodeStep(securityCode);
        completePasswordResetStep(currentPassword, newPassword, newPassword);
    }
}