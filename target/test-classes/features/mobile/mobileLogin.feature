@MOBILE
Feature: Mobile App Login

  Scenario: Successful login with valid credentials
    Given I launch the mobile app
    When I input username "Admin" and password "admin123"
    And I tap login
    Then I should be on the mobile dashboard
