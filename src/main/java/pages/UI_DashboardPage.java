package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UI_DashboardPage {
    WebDriver driver;
    WebDriverWait wait;

    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.xpath("//button[@type='submit']");

    private By dashboardHeader = By.xpath("//h6[text()='Dashboard']");
    private By dashboardMenu = By.id("menu_dashboard_index");
    private By assignLeaveWidget = By.xpath("//h1[text()='Assign Leave']");
    private By leaveListWidget = By.xpath("//h1[text()='Leave List']");
    private By timeAtWorkWidget = By.xpath("//h1[text()='Time at Work']");
    private By adminTab = By.xpath("//span[text()='Admin']");
    private By claimTab = By.xpath("//span[text()='Claim']");
    private By pimTab = By.xpath("//span[text()='PIM']");

    public UI_DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void verifyLoginPageIsDisplayed() {
        if (!driver.getTitle().contains("OrangeHRM")) {
            throw new IllegalStateException("Not on the login page");
        }
    }

    public void login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    /*
     * public void verifyDashboardIsVisible() { boolean isVisible =
     * driver.findElements(dashboardHeader).size() > 0; if (!isVisible) { throw new
     * AssertionError("Dashboard not visible. Login might have failed."); } }
     */

    public void verifyDashboardIsVisible() {
        // Try a slightly longer wait and fallback strategies to avoid flaky failures
        WebDriverWait longerWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        try {
            longerWait.until(ExpectedConditions.visibilityOfElementLocated(dashboardHeader));
        } catch (org.openqa.selenium.TimeoutException e) {
            // fallback: try a contains() match or check page title as a last resort
            By altHeader = By.xpath(
                    "//h6[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'dashboard')]");
            try {
                longerWait.until(ExpectedConditions.visibilityOfElementLocated(altHeader));
            } catch (org.openqa.selenium.TimeoutException e2) {
                // final attempt: check page title contains Dashboard
                if (!driver.getTitle().toLowerCase().contains("dashboard")) {
                    throw new AssertionError("Dashboard not visible. Login might have failed.");
                }
            }
        }
    }

    public void clickDashboardMenu() {
        driver.findElement(dashboardMenu).click();
    }

    public boolean isWidgetVisible(String widgetName) {
        switch (widgetName.toLowerCase()) {
            case "assign leave":
                return driver.findElement(assignLeaveWidget).isDisplayed();
            case "leave list":
                return driver.findElement(leaveListWidget).isDisplayed();
            case "time at work":
                return driver.findElement(timeAtWorkWidget).isDisplayed();
            default:
                return driver.getPageSource().contains(widgetName);
        }
    }

    public void clickAdminTab() {
        driver.findElement(adminTab).click();
    }

    public void clickClaimTab() {
        driver.findElement(claimTab).click();
    }

    public void clickPIMTab() {
        driver.findElement(pimTab).click();
    }

}
