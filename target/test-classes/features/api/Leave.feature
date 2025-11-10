@API
Feature: Leave API Testing

  Scenario: Apply for leave
    Given I set the leave application API endpoint
    When I send a POST request with valid leave details
   # Then I should receive a 200 response confirming leave application
Then I should receive a 404 response confirming leave application
  Scenario: Get leave balance
    Given I set the leave balance API endpoint for employee "123"
    When I send a GET request to fetch leave balance
  #  Then I should receive a 200 response with leave balance data
  Then I should receive a 404 response with leave balance data
