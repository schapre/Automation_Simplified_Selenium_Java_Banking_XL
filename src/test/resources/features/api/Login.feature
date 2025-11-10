@API
Feature: OrangeHRM Login API Testing

  Scenario: Successful login with valid credentials
    Given I set the login API endpoint
    When I send a POST request with valid credentials
    Then I should receive a 302 response from OrangeHRM

  Scenario: Login with invalid credentials
    Given I set the login API endpoint
    When I send a POST request with invalid credentials
    Then I should receive a 302 response from OrangeHRM
