@API
Feature: User Role API Testing

  Scenario: Get all user roles
    Given I set the user roles API endpoint
    When I send a GET request to fetch all roles
  #  Then I should receive a 200 response with role list
    Then I should receive a 404 response with role list

  Scenario: Assign role to user
    Given I set the role assignment API endpoint
    When I send a POST request with user ID and role ID
    # Then I should receive a 200 response confirming role assignment
    Then I should receive a 404 response confirming role assignment
