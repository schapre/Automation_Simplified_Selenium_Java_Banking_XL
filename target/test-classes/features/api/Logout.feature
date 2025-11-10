@API
Feature: Logout API Testing

  Scenario: Logout from OrangeHRM
    Given I set the logout API endpoint
    When I send a POST request to logout
    #Then I should receive a 200 response confirming logout
    Then I should receive a 404 response confirming logout
