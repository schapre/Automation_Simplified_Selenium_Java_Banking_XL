
package stepdefinitions.mobile;

import io.appium.java_client.AppiumDriver;
//import io.appium.java_client.MobileElement;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import utils.DriverFactory;

public class MobileSteps {
    AppiumDriver driver = DriverFactory.getMobileDriver();

    @Given("I launch the mobile app")
    public void iLaunchTheMobileApp() {
        // Assume app is launched in DriverFactory
    }

    @When("I input username {string} and password {string}")
    public void iInputUsernameAndPassword(String user, String pass) {
        driver.findElement(By.id("username")).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(pass);
    }

    @When("I tap login")
    public void iTapLogin() {
        driver.findElement(By.id("login")).click();
    }

    @Then("I should be on the mobile dashboard")
    public void iShouldBeOnTheMobileDashboard() {
        // Validate some element is displayed
    }
}
