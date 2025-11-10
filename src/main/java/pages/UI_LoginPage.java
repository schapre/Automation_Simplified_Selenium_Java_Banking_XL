package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UI_LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    // make login button locator more flexible to handle variations in the app
    private By loginButton = By.xpath(
            "//button[@type='submit'] | //button[contains(., 'Login')] | //button[contains(@class,'oxd-button')] | //button[contains(@id,'login')]");

    public UI_LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        driver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        // wait until the login button is clickable then click
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        driver.findElement(loginButton).click();
    }

    public void verifyLoginPageIsDisplayed() {
        if (!driver.getTitle().contains("OrangeHRM")) {
            throw new IllegalStateException("Not on the login page");
        }
    }

}
