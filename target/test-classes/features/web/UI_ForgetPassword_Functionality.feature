@WEB
Feature: Forget Password Functionality Testing
  As a user who has forgotten their password
  I want to be able to reset my password securely
  So that I can regain access to my account

  Background:
    Given I am on the forget password page

  @TC_Forget_01
  Scenario: TC_Forget_01 - Validate Error message if Email address left blank
    When I leave the email address field blank
    And I click on 'Continue' button
    Then I should see error message 'Email is required'

  @TC_Forget_02  
  Scenario: TC_Forget_02 - Validate Error message if wrong Email address entered
    When I enter wrong email address "wrongemail@test.com"
    And I click on 'Continue' button
    Then I should see error message 'Email not found in our records'

  @TC_Forget_03
  Scenario: TC_Forget_03 - Validate Error message if Secret Code left blank
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I leave the secret code field blank
    And I click on 'Verify Code' button
    Then I should see error message 'Secret code is required'

  @TC_Forget_04
  Scenario: TC_Forget_04 - Validate Error message if wrong Secret Code entered
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter wrong secret code "WRONG123"
    And I click on 'Verify Code' button
    Then I should see error message 'Invalid secret code'

  @TC_Forget_05
  Scenario: TC_Forget_05 - Validate Error message if Current Password left blank
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I leave the current password field blank
    And I enter new password "NewBank@456"
    And I enter confirm password "NewBank@456"
    And I click on 'Reset Password' button
    Then I should see error message 'Current password is required'

  @TC_Forget_06
  Scenario: TC_Forget_06 - Validate Error message if New Password left blank
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I leave the new password field blank
    And I enter confirm password "NewBank@456"
    And I click on 'Reset Password' button
    Then I should see error message 'New password is required'

  @TC_Forget_07
  Scenario: TC_Forget_07 - Validate Error message if Confirm New Password left blank
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I enter new password "NewBank@456"
    And I leave the confirm password field blank
    And I click on 'Reset Password' button
    Then I should see error message 'Please confirm your new password'

  @TC_Forget_08
  Scenario: TC_Forget_08 - Validate error message if New and Confirm Password do not match
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I enter new password "NewBank@456"
    And I enter different confirm password "DifferentBank@789"
    And I click on 'Reset Password' button
    Then I should see error message 'New passwords do not match'

  @TC_Forget_09
  Scenario: TC_Forget_09 - Validate error message if New and Confirm Password do not meet password criteria
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I enter weak new password "123"
    And I enter same weak confirm password "123"
    And I click on 'Reset Password' button
    Then I should see error message 'Password does not meet requirements'

  @TC_Forget_10
  Scenario: TC_Forget_10 - Validate password strength indicator when some criteria are met
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I enter partial criteria new password "Newpassword1"
    And I enter same partial criteria confirm password "Newpassword1"
    Then I should see password requirements are displayed
    And the password requirements should show current status

  @TC_Forget_11
  Scenario: TC_Forget_11 - Validate successful password reset with a valid New Password
    When I enter valid email address "user@premiumbank.com"
    And I click on 'Continue' button
    And I enter correct secret code "BANK1234"
    And I click on 'Verify Code' button
    And I enter current password "Bank@123"
    And I enter valid new password "NewBank@456"
    And I enter same valid confirm password "NewBank@456"
    And I click on 'Reset Password' button
    Then I should see success message 'Password Changed. Your password has been updated successfully.'
    And I should see 'Return to Login' button displayed