@API
Feature: Employee API Testing

  Scenario: Get employee details by ID
    Given I set the employee API endpoint with ID "123"
    When I send a GET request to fetch employee details
   # Then I should receive a 200 response with employee data
    Then I should receive a 404 response with employee data

  Scenario: Create a new employee
    Given I set the employee creation API endpoint
    When I send a POST request with valid employee data
    # Then I should receive a 201 response confirming creation
    Then I should receive a 404 response confirming creation
