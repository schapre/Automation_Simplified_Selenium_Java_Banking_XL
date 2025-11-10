package stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class EmployeeSteps {

    private String endpoint;
    private Response response;

    @Given("I set the employee API endpoint with ID {string}")
    public void setEmployeeEndpoint(String id) {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/employees/" + id;
    }

    @When("I send a GET request to fetch employee details")
    public void getEmployeeDetails() {
        response = given().get(endpoint);
    }

    @Given("I set the employee creation API endpoint")
    public void setEmployeeCreationEndpoint() {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/employees";
    }

    @When("I send a POST request with valid employee data")
    public void createEmployee() {
        response = given()
            .header("Content-Type", "application/json")
            .body("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\"}")
            .post(endpoint);
    }

    @Then("I should receive a {int} response with employee data")
    @Then("I should receive a {int} response confirming creation")
    public void verifyEmployeeResponse(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }
}
