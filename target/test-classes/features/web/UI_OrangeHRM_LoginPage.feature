@WEB
Feature: Login Page
  As a user
  I want to be able to log in
  So that I can access my dashboard

  Scenario Outline: Successful login with valid credentials
    Given the application is launched
    And the user is on the login page
    And the user enters username "<username>" and password "<password>"
    And the user clicks the login button
    Then the user should be logged in and directed to the dashboard

    Examples:
      | username | password |
      | Admin    | admin123 |
