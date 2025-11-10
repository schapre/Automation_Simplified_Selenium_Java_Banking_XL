package stepdefinitions.api;

import io.cucumber.java.en.*;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

public class LeaveSteps {

    private String endpoint;
    private Response response;

    @Given("I set the leave application API endpoint")
    public void setLeaveApplicationEndpoint() {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/leave/apply";
    }

    @When("I send a POST request with valid leave details")
    public void applyForLeave() {
        response = given()
            .header("Content-Type", "application/json")
            .body("{\"employeeId\":\"123\",\"leaveType\":\"Annual\",\"fromDate\":\"2025-08-10\",\"toDate\":\"2025-08-12\"}")
            .post(endpoint);
    }

    @Given("I set the leave balance API endpoint for employee {string}")
    public void setLeaveBalanceEndpoint(String employeeId) {
        endpoint = "https://opensource-demo.orangehrmlive.com/api/leave/balance/" + employeeId;
    }

    @When("I send a GET request to fetch leave balance")
    public void getLeaveBalance() {
        response = given().get(endpoint);
    }

    @Then("I should receive a {int} response with leave balance data")
    @Then("I should receive a {int} response confirming leave application")
    public void verifyLeaveResponse(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }
}
