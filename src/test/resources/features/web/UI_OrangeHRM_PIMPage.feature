@WEB
Feature: Admin Page. As an admin I want to add a user in the users list.

  Background:
   Given the application is launched
    And the user is on the login page
And the user clicks the login button
    Then the user should be logged in and directed to the dashboard
 
  Scenario Outline: Adding user to users list
    When the admin clicks the PIM button
    And the admin clicks the add-user button PIMPage
    And the admin enters first name "<firstname>"
    And the admin enters middle name "<middlename>"
    And the admin enters last name "<l>"
    Then on clicking the add user button the should be able to add a user for PIM Page

    Examples:
     
      | firstname   | middlename    | middlename    | 
      | Jean        | Paul          | math          | 
      | Paul        | Jean          | math          | 
      #| T              | pqxyz_3333  | pqxyz@123  | pqxyz@123    |
      #| W              | butter_6666 | butter@123 | butter@123   |
