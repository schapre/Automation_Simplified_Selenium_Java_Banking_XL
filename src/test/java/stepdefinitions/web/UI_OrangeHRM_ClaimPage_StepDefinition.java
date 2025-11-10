package stepdefinitions.web;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import pages.UI_ClaimPage;
import pages.UI_DashboardPage;
import utils.DriverFactory;

public class UI_OrangeHRM_ClaimPage_StepDefinition {

    WebDriver driver = DriverFactory.getWebDriver();
    UI_DashboardPage dashboardPage = new UI_DashboardPage(driver);
    UI_ClaimPage claimPage = new UI_ClaimPage(driver);

    @When("the user navigates to the Claim module")
    public void the_user_navigates_to_the_claim_module() {
        dashboardPage.clickClaimTab();
    }

    @And("the user clicks on create claim")
    public void the_user_clicks_on_create_claim() {
        claimPage.clickCreateClaimButton();
    }

    @And("the user selects claim type {string}")
    public void the_user_selects_claim_type(String type) {
        claimPage.selectClaimType(type);
    }

    @And("the user enters claim amount {string} and description {string}")
    public void the_user_enters_claim_amount_and_description(String amount, String description) {
        claimPage.enterClaimAmount(amount);
        claimPage.enterClaimDescription(description);
    }

    @And("the user submits the claim")
    public void the_user_submits_the_claim() {
        claimPage.clickSubmitButton();
    }

    @Then("the claim should be successfully submitted")
    public void the_claim_should_be_successfully_submitted() {
        claimPage.verifyClaimSubmission();
    }
}
