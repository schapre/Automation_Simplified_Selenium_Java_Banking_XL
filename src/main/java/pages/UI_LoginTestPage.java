package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class UI_LoginTestPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Login page elements
    private By emailField = By.xpath("//input[@placeholder='Enter your email']");
    private By passwordField = By.xpath("//input[@placeholder='Enter your password']");
    private By loginButton = By.xpath("//button[text()='Sign in']");
    private By forgotPasswordLink = By.xpath("//a[contains(text(),'Forgot password')]");
    private By registerNowLink = By.xpath("//a[contains(text(),'Register now')]");
    
    // Success/Error message elements - TesterBud may handle differently
    private By successMessage = By.xpath("//div[contains(@class,'success') or contains(@class,'alert-success') or contains(text(),'Success') or contains(text(),'Welcome')]");
    private By backToLoginButton = By.xpath("//button[contains(text(),'Back') or contains(text(),'Login')]");
    private By emailRequiredError = By.xpath("//div[contains(@class,'error') or contains(@class,'alert-danger') or contains(text(),'required') or contains(text(),'Email')]");
    private By passwordRequiredError = By.xpath("//div[contains(@class,'error') or contains(@class,'alert-danger') or contains(text(),'required') or contains(text(),'Password')]");
    private By invalidCredentialsError = By.xpath("//div[contains(@class,'error') or contains(@class,'alert-danger') or contains(text(),'Invalid') or contains(text(),'incorrect')]");
    
    public UI_LoginTestPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    // Navigation methods
    public void navigateToLoginPage() {
        driver.get("https://testerbud.com/practice-login-form");
    }
    
    // Input methods
    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        WebElement emailElement = driver.findElement(emailField);
        emailElement.clear();
        emailElement.sendKeys(email);
    }
    
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        WebElement passwordElement = driver.findElement(passwordField);
        passwordElement.clear();
        passwordElement.sendKeys(password);
    }
    
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        WebElement loginButtonElement = driver.findElement(loginButton);
        
        // Scroll the element into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginButtonElement);
        
        try {
            // Try regular click first
            loginButtonElement.click();
        } catch (ElementClickInterceptedException e) {
            // If regular click fails, use JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButtonElement);
        }
    }
    
    // Verification methods
    public boolean isEmailFieldDisplayed() {
        try {
            return driver.findElement(emailField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isPasswordFieldDisplayed() {
        try {
            return driver.findElement(passwordField).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isLoginButtonDisplayed() {
        try {
            return driver.findElement(loginButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isForgotPasswordLinkDisplayed() {
        try {
            return driver.findElement(forgotPasswordLink).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isRegisterNowLinkDisplayed() {
        try {
            return driver.findElement(registerNowLink).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isLoginButtonEnabled() {
        try {
            return driver.findElement(loginButton).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isPasswordMasked() {
        try {
            WebElement passwordElement = driver.findElement(passwordField);
            String inputType = passwordElement.getAttribute("type");
            return "password".equals(inputType);
        } catch (Exception e) {
            return false;
        }
    }
    
    // Success message verification
    public boolean isSuccessMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return driver.findElement(successMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getSuccessMessageText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return driver.findElement(successMessage).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public boolean isBackToLoginButtonDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(backToLoginButton));
            return driver.findElement(backToLoginButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Error message verification methods
    public boolean isInvalidCredentialsErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(invalidCredentialsError));
            return driver.findElement(invalidCredentialsError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getInvalidCredentialsErrorText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(invalidCredentialsError));
            return driver.findElement(invalidCredentialsError).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public boolean isEmailPasswordRequiredErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailRequiredError));
            return driver.findElement(emailRequiredError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getEmailPasswordRequiredErrorText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailRequiredError));
            return driver.findElement(emailRequiredError).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public boolean isPasswordRequiredErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordRequiredError));
            return driver.findElement(passwordRequiredError).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getPasswordRequiredErrorText() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordRequiredError));
            return driver.findElement(passwordRequiredError).getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    // Browser validation message (for HTML5 validation)
    public String getBrowserValidationMessage() {
        try {
            WebElement emailElement = driver.findElement(emailField);
            return (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", emailElement);
        } catch (Exception e) {
            return "";
        }
    }
    
    // Clear fields
    public void clearEmailField() {
        WebElement emailElement = driver.findElement(emailField);
        emailElement.clear();
    }
    
    public void clearPasswordField() {
        WebElement passwordElement = driver.findElement(passwordField);
        passwordElement.clear();
    }
}