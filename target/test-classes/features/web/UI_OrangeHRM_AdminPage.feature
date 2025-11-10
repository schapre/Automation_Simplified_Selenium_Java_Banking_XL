@WEB
Feature: Admin Page. As an admin I want to add a user in the users list.

  Background:
  
    Given the application is launched
    And the user is on the login page
    And the user clicks the login button
    Then the user should be logged in and directed to the dashboard
    
 
  Scenario Outline: Adding user to users list
    When the admin clicks the admin button
    And the admin clicks the add-user button AdminPage
    And the admin selects the user role and enters employee name "<EmployeeName>"
    And the admin selects the status and enters username "<uname>"
    And the admin enters password "<password>" and confirm password "<confirmPass>"
    Then on clicking the add user button the admin should be able to add a user

    Examples:
     
      | EmployeeName   | uname        | password    | confirmPass   |
      | J              | Prity_1144a  | Prity@123a  | Prity@123a    |
      | Random         | Random       | Random      | Random       |
      #| T              | pqxyz_3333  | pqxyz@123  | pqxyz@123    |
      #| W              | butter_6666 | butter@123 | butter@123   |
