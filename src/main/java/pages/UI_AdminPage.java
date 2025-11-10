package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UI_AdminPage {
    WebDriver driver;
    WebDriverWait wait;
    
    private By addUserButton = By.xpath("//button[normalize-space()='Add']");
    private By userRoleDropdown = By.xpath("//label[text()='User Role']/following::div[1]");
    private By employeeNameField = By.xpath("//input[@placeholder='Type for hints...']");
    private By statusDropdown = By.xpath("//label[text()='Status']/following::div[1]");
    private By usernameField = By.xpath("//label[text()='Username']/following::input[1]");
    private By passwordField = By.xpath("//label[text()='Password']/following::input[1]");
    private By confirmPasswordField = By.xpath("//label[text()='Confirm Password']/following::input[1]");
    private By saveButton = By.xpath("//button[normalize-space()='Save']");
    private By successToast = By.xpath("//div[contains(@class,'oxd-toast')]");

    public UI_AdminPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickAddUserButton() {
        driver.findElement(addUserButton).click();
    }

    public void selectUserRole(String role) {
        driver.findElement(userRoleDropdown).click();
        driver.findElement(By.xpath("//span[text()='" + role + "']")).click();
    }

    public void enterEmployeeName(String name) {
        driver.findElement(employeeNameField).sendKeys(name);
        driver.findElement(By.xpath("//div[@role='option']")).click(); // Select first suggestion
    }

    public void selectStatus(String status) {
        driver.findElement(statusDropdown).click();
        driver.findElement(By.xpath("//span[text()='" + status + "']")).click();
    }

    public void enterUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        driver.findElement(confirmPasswordField).sendKeys(confirmPassword);
    }

    public void clickSaveButton() {
        driver.findElement(saveButton).click();
    }

    public void verifyUserAdded() {
        boolean isToastVisible = driver.findElements(successToast).size() > 0;
        if (!isToastVisible) {
            throw new AssertionError("User not added successfully.");
        }
    }
}
