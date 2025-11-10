@WEB
Feature: Claim Page. As I want to create a claim request.

  Background:
   Background:
  
    Given the application is launched
    And the user is on the login page
    And the user clicks the login button
    Then the user should be logged in and directed to the dashboard

  Scenario Outline: Submit a new claim request
    When the user navigates to the Claim module
    And the user clicks on create claim
    And the user selects claim type "<type>"
    And the user enters claim amount "<amount>" and description "<description>"
    And the user submits the claim
    Then the claim should be successfully submitted

    Examples:
      | type     | amount | description         |
      | Travel   | 1500   | Client visit travel |
      | Medical  | 800    | Health checkup      |
