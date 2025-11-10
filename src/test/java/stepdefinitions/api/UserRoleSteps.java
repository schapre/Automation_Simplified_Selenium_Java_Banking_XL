package stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class UserRoleSteps {

    private String endpoint;
    private Response response;

    @Given("I set the user roles API endpoint")
    public void setUserRolesEndpoint() {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/roles";
    }

    @When("I send a GET request to fetch all roles")
    public void getAllRoles() {
        response = given().get(endpoint);
    }

    @Given("I set the role assignment API endpoint")
    public void setRoleAssignmentEndpoint() {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/roles/assign";
    }

    @When("I send a POST request with user ID and role ID")
    public void assignRoleToUser() {
        response = given()
            .header("Content-Type", "application/json")
            .body("{\"userId\":\"123\",\"roleId\":\"admin\"}")
            .post(endpoint);
    }

    @Then("I should receive a {int} response with role list")
    @Then("I should receive a {int} response confirming role assignment")
    public void verifyRoleResponse(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }
}
