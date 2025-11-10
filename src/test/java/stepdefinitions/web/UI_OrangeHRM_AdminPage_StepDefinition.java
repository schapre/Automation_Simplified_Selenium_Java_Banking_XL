package stepdefinitions.web;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.UI_AdminPage;
import pages.UI_DashboardPage;
import utils.DriverFactory;

public class UI_OrangeHRM_AdminPage_StepDefinition {

    WebDriver driver = DriverFactory.getWebDriver();
    UI_DashboardPage dashboardPage = new UI_DashboardPage(driver);
    UI_AdminPage adminPage = new UI_AdminPage(driver);

    @When("the admin clicks the admin button")
    public void the_admin_clicks_the_admin_button() {
        dashboardPage.clickAdminTab();
    }

    @And("the admin clicks the add-user button AdminPage")
    public void the_admin_clicks_the_add_user_button_admin_page() {
        adminPage.clickAddUserButton();
    }

    @And("the admin selects the user role and enters employee name {string}")
    public void the_admin_selects_user_role_and_enters_employee_name(String employeeName) {
        adminPage.selectUserRole("ESS");
        adminPage.enterEmployeeName(employeeName);
    }

    @And("the admin selects the status and enters username {string}")
    public void the_admin_selects_status_and_enters_username(String username) {
        adminPage.selectStatus("Enabled");
        adminPage.enterUsername(username);
    }

    @And("the admin enters password {string} and confirm password {string}")
    public void the_admin_enters_password_and_confirm_password(String password, String confirmPassword) {
        adminPage.enterPassword(password);
        adminPage.enterConfirmPassword(confirmPassword);
    }

    @Then("on clicking the add user button the admin should be able to add a user")
    public void on_clicking_the_add_user_button_the_admin_should_be_able_to_add_a_user() {
        adminPage.clickSaveButton();
        adminPage.verifyUserAdded();
    }
}
