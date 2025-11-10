package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UI_ClaimPage {
    WebDriver driver;
    WebDriverWait wait;

    private By createClaimButton = By.xpath("//button[normalize-space()='Assign Claim']");
    private By claimTypeDropdown = By.xpath("//label[text()='Claim Type']/following::div[1]");
    private By claimAmountField = By.xpath("//input[@name='amount']");
    private By claimDescriptionField = By.xpath("//textarea[@name='description']");
    private By submitButton = By.xpath("//button[normalize-space()='Submit']");
    private By successToast = By.xpath("//div[contains(@class,'oxd-toast')]");

    public UI_ClaimPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickCreateClaimButton() {
        driver.findElement(createClaimButton).click();
    }

    public void selectClaimType(String type) {
        driver.findElement(claimTypeDropdown).click();
        driver.findElement(By.xpath("//span[text()='" + type + "']")).click();
    }

    public void enterClaimAmount(String amount) {
        driver.findElement(claimAmountField).sendKeys(amount);
    }

    public void enterClaimDescription(String description) {
        driver.findElement(claimDescriptionField).sendKeys(description);
    }

    public void clickSubmitButton() {
        driver.findElement(submitButton).click();
    }

    public void verifyClaimSubmission() {
        boolean isToastVisible = driver.findElements(successToast).size() > 0;
        if (!isToastVisible) {
            throw new AssertionError("Claim submission failed.");
        }
    }
}
