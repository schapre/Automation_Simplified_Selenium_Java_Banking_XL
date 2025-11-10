package stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;
import org.testng.Assert;
import utils.TestContext;

public class LoginSteps {



    private final TestContext testContext;

    public LoginSteps(TestContext context) {
        this.testContext = context;
    }

    @Given("I set the login API endpoint")
    public void iSetTheLoginAPIEndpoint() {
        RestAssured.baseURI = "https://opensource-demo.orangehrmlive.com/"; // Replace with actual endpoint
    }

    @When("I send a POST request with valid credentials")
    public void iSendPostRequestWithValidCredentials() {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"Admin\", \"password\": \"admin123\" }") // Replace with actual payload
            .post();

        testContext.setResponse(response);
    }

    @When("I send a POST request with invalid credentials")
    public void iSendPostRequestWithInvalidCredentials() {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body("{ \"username\": \"Admin\", \"password\": \"wrongpass\" }") // Replace with actual payload
            .post();

        testContext.setResponse(response);
    }

    @Then("I should receive a {int} response from OrangeHRM")
    public void iShouldReceiveAResponseFromOrangeHRM(Integer expectedStatusCode) {
        Response response = testContext.getResponse();
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode.intValue(), 
            "Expected status code does not match actual.");
    }
}
