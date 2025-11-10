package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

public class ElementDiscoveryHelper {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    public ElementDiscoveryHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    
    /**
     * Find working selector for Add button on Admin page
     */
    public String findAdminAddButton() {
        System.out.println("\n=== Searching for Admin Page Add Button ===");
        
        // Wait for page to load
        wait.until(ExpectedConditions.urlContains("admin"));
        
        String[] selectors = {
            "button[class*='oxd-button'][class*='secondary']",
            "button[class*='oxd-button--secondary']",
            "button.oxd-button.oxd-button--medium.oxd-button--secondary",
            "button:contains('Add')",
            "//button[contains(text(),'Add')]",
            "//button[normalize-space()='Add']",
            "//button[@type='button'][contains(@class,'oxd-button')]",
            ".oxd-button--secondary",
            "[data-v-10d463b7][data-v-838b2bc8]"
        };
        
        return findWorkingSelector(selectors, "Admin Add Button");
    }
    
    /**
     * Find working selector for Add button on PIM page
     */
    public String findPIMAddButton() {
        System.out.println("\n=== Searching for PIM Page Add Button ===");
        
        // Wait for page to load
        wait.until(ExpectedConditions.urlContains("pim"));
        
        String[] selectors = {
            "button[class*='oxd-button'][class*='secondary']",
            "button.oxd-button--secondary",
            "//button[contains(text(),'Add')]",
            "//button[normalize-space()='+ Add']",
            "//button[text()='Add']",
            "//button[@type='button'][contains(@class,'oxd-button')]",
            ".oxd-button--secondary",
            "button[class*='oxd-button--medium']"
        };
        
        return findWorkingSelector(selectors, "PIM Add Button");
    }
    
    /**
     * Find working selector for Create button on Claim page
     */
    public String findClaimCreateButton() {
        System.out.println("\n=== Searching for Claim Page Create Button ===");
        
        // Wait for page to load
        wait.until(ExpectedConditions.urlContains("claim"));
        
        String[] selectors = {
            "button[class*='oxd-button'][class*='secondary']",
            "button.oxd-button--secondary",
            "//button[contains(text(),'Create')]",
            "//button[normalize-space()='Create']",
            "//button[contains(@class,'oxd-button--secondary')]",
            "//button[@type='button'][contains(@class,'oxd-button')]",
            ".oxd-button--secondary"
        };
        
        return findWorkingSelector(selectors, "Claim Create Button");
    }
    
    /**
     * Test multiple selectors and return the first working one
     */
    private String findWorkingSelector(String[] selectors, String elementName) {
        for (String selector : selectors) {
            try {
                WebElement element;
                if (selector.startsWith("//") || selector.startsWith("(")) {
                    element = driver.findElement(By.xpath(selector));
                } else {
                    element = driver.findElement(By.cssSelector(selector));
                }
                
                if (element != null && element.isDisplayed() && element.isEnabled()) {
                    System.out.println("✅ WORKING SELECTOR for " + elementName + ": " + selector);
                    System.out.println("   Element Text: '" + element.getText() + "'");
                    System.out.println("   Element Tag: " + element.getTagName());
                    System.out.println("   Element Classes: " + element.getAttribute("class"));
                    return selector;
                }
            } catch (Exception e) {
                System.out.println("❌ Failed: " + selector + " - " + e.getMessage());
            }
        }
        
        System.out.println("⚠️  No working selector found for " + elementName);
        return null;
    }
    
    /**
     * Print all buttons found on the current page for debugging
     */
    public void printAllButtons() {
        System.out.println("\n=== All Buttons on Current Page ===");
        try {
            List<WebElement> buttons = driver.findElements(By.tagName("button"));
            for (int i = 0; i < buttons.size(); i++) {
                WebElement button = buttons.get(i);
                System.out.println("Button " + (i + 1) + ":");
                System.out.println("  Text: '" + button.getText() + "'");
                System.out.println("  Class: " + button.getAttribute("class"));
                System.out.println("  Type: " + button.getAttribute("type"));
                System.out.println("  Visible: " + button.isDisplayed());
                System.out.println("  Enabled: " + button.isEnabled());
                System.out.println("  ---");
            }
        } catch (Exception e) {
            System.out.println("Error finding buttons: " + e.getMessage());
        }
    }
}