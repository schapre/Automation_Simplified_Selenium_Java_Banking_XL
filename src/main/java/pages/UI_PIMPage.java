package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UI_PIMPage {
    WebDriver driver;
    WebDriverWait wait;
    
    private By addButton = By.xpath("//button[normalize-space()='Add']");
    private By firstNameField = By.name("firstName");
    private By middleNameField = By.name("middleName");
    private By lastNameField = By.name("lastName");
    private By saveButton = By.xpath("//button[normalize-space()='Save']");
    private By successToast = By.xpath("//div[contains(@class,'oxd-toast')]");

    public UI_PIMPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickAddUserButton() {
        driver.findElement(addButton).click();
    }

    public void enterFirstName(String firstName) {
        driver.findElement(firstNameField).sendKeys(firstName);
    }

    public void enterMiddleName(String middleName) {
        driver.findElement(middleNameField).sendKeys(middleName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(lastNameField).sendKeys(lastName);
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
