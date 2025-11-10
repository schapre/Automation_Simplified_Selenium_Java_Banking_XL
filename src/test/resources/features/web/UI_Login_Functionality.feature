@WEB
Feature: Login Functionality Testing
  As a user
  I want to test various login scenarios
  So that I can ensure the login functionality works correctly

  Background:
    Given I navigate to the login page

  @TC_Basic_01
  Scenario: TC_Basic_01 - Verify login with valid credentials
    When I enter valid email id "user@premiumbank.com" and password "Bank@123"
    And I click on the login button
    Then I should see successful message "Login Successful"
    And I should see "Back to Login" button

  @TC_Basic_02
  Scenario: TC_Basic_02 - Verify login with invalid credentials
    When I enter invalid email id "invalid@example.com" or password "wrongPassword"
    And I click on the login button
    Then error message "Invalid email id and password" should be displayed

  @TC_Basic_03
  Scenario: TC_Basic_03 - Check UI elements of the login page
    Then I should see Email Address field is displayed
    And I should see Password field is displayed
    And I should see Login button is displayed
    And I should see Forget Password Link is displayed
    And I should see Register now Link is displayed

  @TC_Basic_04
  Scenario: TC_Basic_04 - Verify login button is enabled and validate error message when fields are empty
    When I keep username and password fields empty
    And I click the login button
    Then Login button should be enabled
    And I should get error message "Email and Password are required"

  @TC_Basic_05
  Scenario: TC_Basic_05 - Verify password field is masked
    When I enter password "testPassword123" in the password field
    Then Password should be masked with dots

  @TC_Basic_06
  Scenario: TC_Basic_06 - Verify error message when username is entered but password field is left blank
    When I enter valid username "valid@example.com" and keep password field empty
    And I click the login button
    Then error message "Password is required" should be displayed

  @TC_Basic_07
  Scenario: TC_Basic_07 - Verify error message when email id entered in invalid format
    When I enter invalid format username "abc123"
    And I click the login button
    Then error message "Please include an '@' in the email address. 'abc123' is missing '@'" should be displayed