@WEB
Feature: User Registration Functionality

  Background:
    Given User navigates to TesterBud registration page

  @TC_Registration_01
  Scenario: TC_Registration_01 - Verify successful user registration
    Given User is on the registration page
    When User enters valid email "test.user@testerbud.com"
    And User enters valid password "TestBud@123"
    And User enters confirm password "TestBud@123"
    And User clicks on Register button
    Then User should see registration success message
    And User should see "Go to Login" button
    And User should be redirected to success page or login page

  @TC_Registration_02
  Scenario: TC_Registration_02 - Validate registration with empty email field
    Given User is on the registration page
    When User leaves email field empty
    And User enters valid password "TestBud@123"
    And User enters confirm password "TestBud@123"
    And User clicks on Register button
    Then User should see "Email is required" error message
    And User should remain on the registration page

  @TC_Registration_03
  Scenario: TC_Registration_03 - Validate registration with empty password field
    Given User is on the registration page
    When User enters valid email "test.user@testerbud.com"
    And User leaves password field empty
    And User enters confirm password "TestBud@123"
    And User clicks on Register button
    Then User should see "Password is required" error message
    And User should remain on the registration page

  @TC_Registration_04
  Scenario: TC_Registration_04 - Validate registration with empty confirm password field
    Given User is on the registration page
    When User enters valid email "test.user@testerbud.com"
    And User enters valid password "TestBud@123"
    And User leaves confirm password field empty
    And User clicks on Register button
    Then User should see "Confirm Password is required" error message
    And User should remain on the registration page

  @TC_Registration_05
  Scenario: TC_Registration_05 - Validate registration with invalid email format
    Given User is on the registration page
    When User enters invalid email "invalid-email"
    And User enters valid password "TestBud@123"
    And User enters confirm password "TestBud@123"
    And User clicks on Register button
    Then User should see invalid email format error message
    And User should remain on the registration page

  @TC_Registration_06
  Scenario: TC_Registration_06 - Validate registration when password and confirm password do not match
    Given User is on the registration page
    When User enters valid email "test.user@testerbud.com"
    And User enters valid password "TestBud@123"
    And User enters confirm password "DifferentPassword@123"
    And User clicks on Register button
    Then User should see "Passwords do not match" error message
    And User should remain on the registration page

  @TC_Registration_07
  Scenario: TC_Registration_07 - Validate password strength requirements are displayed when user enters a password
    Given User is on the registration page
    When User enters password "weak"
    Then User should see password strength requirements
    And Password strength indicators should be visible

  @TC_Registration_08
  Scenario: TC_Registration_08 - Validate registration with weak password that does not meet strength requirements
    Given User is on the registration page
    When User enters valid email "test.user@testerbud.com"
    And User enters weak password "weak"
    And User enters confirm password "weak"
    And User clicks on Register button
    Then User should see "Password does not meet requirements" error message
    And User should remain on the registration page

  @TC_Registration_09
  Scenario: TC_Registration_09 - Validate password strength indicators are visible when strong password is entered
    Given User is on the registration page
    When User enters strong password "StrongPass@123"
    Then User should see password strength requirements
    And Password strength indicators should be visible

  @TC_Registration_10
  Scenario: TC_Registration_10 - Validate clicking 'Sign in' link successfully navigate user to Login Page
    Given User is on the registration page
    When User clicks on "Sign in" link
    Then User should be redirected to login page
    And User should see login form with email and password fields
    And Login page URL should contain "practice-login-form"