package stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class LogoutSteps {

    private String endpoint;
    private Response response;

    @Given("I set the logout API endpoint")
    public void setLogoutEndpoint() {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/logout";
    }

    @When("I send a POST request to logout")
    public void sendLogoutRequest() {
        response = given().post(endpoint);
    }

    @Then("I should receive a {int} response confirming logout")
    public void verifyLogoutResponse(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }
}
