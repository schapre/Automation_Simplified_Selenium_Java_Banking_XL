@database @smoke
Feature: Database Basic Operations
  As a test automation engineer
  I want to perform basic CRUD operations on the database
  So that I can verify database functionality

  Background:
    Given I have a database connection

  @create @positive
  Scenario: Create a new user record
    When I insert data into table "users" with values:
      | id       | 1                    |
      | name     | John Doe             |
      | email    | john.doe@example.com |
      | status   | active               |
      | age      | 30                   |
    Then 1 records should be affected
    And I check if record exists in table "users" where "id = 1"
    And the record should exist

  @read @positive
  Scenario: Read user data from database
    When I execute the query "SELECT * FROM users WHERE status = 'active'"
    Then I should get 1 results
    And the results should contain:
      | id | name     | email                | status | age |
      | 1  | John Doe | john.doe@example.com | active | 30  |
    And I print the query results

  @read @positive
  Scenario: Read user data with parameters
    When I execute the query "SELECT * FROM users WHERE age > ? AND status = ?" with parameters:
      | 25     |
      | active |
    Then I should get 1 results
    And the results should contain:
      | id | name     | email                | status | age |
      | 1  | John Doe | john.doe@example.com | active | 30  |

  @update @positive
  Scenario: Update user information
    When I update table "users" set "email = 'john.updated@example.com', age = 31" where "id = 1"
    Then 1 records should be affected
    And I validate data in table "users" where "id = 1" has values:
      | email | john.updated@example.com |
      | age   | 31                       |
      | name  | John Doe                 |

  @count @positive
  Scenario: Count records in table
    When I count records in table "users"
    Then I should get count 1
    When I count records in table "users" where "status = 'active'"
    Then I should get count 1
    When I count records in table "users" where "status = 'inactive'"
    Then I should get count 0

  @delete @positive
  Scenario: Delete user record
    When I delete from table "users" where "id = 1"
    Then 1 records should be affected
    And I check if record exists in table "users" where "id = 1"
    And the record should not exist
    When I count records in table "users"
    Then I should get count 0

  @cleanup
  Scenario: Clean up test data
    When I truncate table "users"
    When I count records in table "users"
    Then I should get count 0