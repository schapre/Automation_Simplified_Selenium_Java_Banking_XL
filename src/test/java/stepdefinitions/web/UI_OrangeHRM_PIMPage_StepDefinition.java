package stepdefinitions.web;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.UI_DashboardPage;
import pages.UI_PIMPage;
import utils.DriverFactory;

public class UI_OrangeHRM_PIMPage_StepDefinition {

    WebDriver driver = DriverFactory.getWebDriver();
    UI_DashboardPage dashboardPage = new UI_DashboardPage(driver);
    UI_PIMPage pimPage = new UI_PIMPage(driver);

    @When("the admin clicks the PIM button")
    public void the_admin_clicks_the_pim_button() {
        dashboardPage.clickPIMTab();
    }

    @And("the admin clicks the add-user button PIMPage")
    public void the_admin_clicks_the_add_user_button_pim_page() {
        pimPage.clickAddUserButton();
    }

    @And("the admin enters first name {string}")
    public void the_admin_enters_first_name(String firstName) {
        pimPage.enterFirstName(firstName);
    }

    @And("the admin enters middle name {string}")
    public void the_admin_enters_middle_name(String middleName) {
        pimPage.enterMiddleName(middleName);
    }

    @And("the admin enters last name {string}")
    public void the_admin_enters_last_name(String lastName) {
        pimPage.enterLastName(lastName);
    }

    @Then("on clicking the add user button the should be able to add a user for PIM Page")
    public void on_clicking_add_user_button_should_add_user_for_pim_page() {
        pimPage.clickSaveButton();
        pimPage.verifyUserAdded();
    }
}
