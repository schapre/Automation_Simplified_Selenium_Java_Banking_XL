@database @regression
Feature: Database Advanced Operations
  As a test automation engineer
  I want to perform complex database operations and validations
  So that I can verify advanced database functionality

  Background:
    Given I have a database connection

  @batch-operations @positive
  Scenario: Batch insert multiple users
    When I insert data into table "users" with values:
      | id    | 101                     |
      | name  | Alice Johnson           |
      | email | alice.johnson@test.com  |
      | status| active                  |
      | age   | 28                      |
    And I insert data into table "users" with values:
      | id    | 102                     |
      | name  | Bob Smith              |
      | email | bob.smith@test.com     |
      | status| active                 |
      | age   | 35                     |
    And I insert data into table "users" with values:
      | id    | 103                     |
      | name  | Carol Davis            |
      | email | carol.davis@test.com   |
      | status| inactive               |
      | age   | 42                     |
    Then I count records in table "users"
    And I should get count 3

  @filtering @positive
  Scenario Outline: Filter users by different criteria
    When I execute the query "SELECT * FROM users WHERE <filter_column> <operator> '<filter_value>'"
    Then I should get <expected_count> results
    
    Examples:
      | filter_column | operator | filter_value | expected_count |
      | status        | =        | active       | 2              |
      | status        | =        | inactive     | 1              |
      | age          | >        | 30           | 2              |
      | age          | <        | 30           | 1              |
      | name         | LIKE     | %o%          | 2              |

  @data-validation @positive
  Scenario: Validate user data integrity
    When I validate data in table "users" where "id = 101" has values:
      | name   | Alice Johnson          |
      | email  | alice.johnson@test.com |
      | status | active                 |
      | age    | 28                     |
    And I validate data in table "users" where "id = 102" has values:
      | name   | Bob Smith             |
      | email  | bob.smith@test.com    |
      | status | active                |
      | age    | 35                    |
    And I validate data in table "users" where "id = 103" has values:
      | name   | Carol Davis           |
      | email  | carol.davis@test.com  |
      | status | inactive              |
      | age    | 42                    |

  @joins @positive
  Scenario: Test data relationships with joins
    # Insert test orders for users
    When I insert data into table "orders" with values:
      | order_id  | 1001 |
      | user_id   | 101  |
      | product   | Laptop |
      | amount    | 999.99 |
      | status    | completed |
    And I insert data into table "orders" with values:
      | order_id  | 1002 |
      | user_id   | 102  |
      | product   | Mouse |
      | amount    | 25.50 |
      | status    | pending |
    When I execute the query "SELECT u.name, o.product, o.amount FROM users u JOIN orders o ON u.id = o.user_id WHERE o.status = 'completed'"
    Then I should get 1 results
    And the results should contain:
      | name          | product | amount |
      | Alice Johnson | Laptop  | 999.99 |

  @aggregation @positive
  Scenario: Test data aggregation functions
    When I execute the query "SELECT COUNT(*) as user_count FROM users WHERE status = 'active'"
    Then I should get 1 results
    And the results should contain:
      | user_count |
      | 2          |
    
    When I execute the query "SELECT AVG(age) as avg_age FROM users WHERE status = 'active'"
    Then I should get 1 results
    
    When I execute the query "SELECT MAX(age) as max_age, MIN(age) as min_age FROM users"
    Then I should get 1 results
    And the results should contain:
      | max_age | min_age |
      | 42      | 28      |

  @transactions @positive
  Scenario: Test transaction-like operations
    # Update multiple records and verify consistency
    When I update table "users" set "status = 'updated'" where "age > 30"
    Then 2 records should be affected
    
    When I count records in table "users" where "status = 'updated'"
    Then I should get count 2
    
    When I count records in table "users" where "status = 'active'"
    Then I should get count 1

  @negative @error-handling
  Scenario: Handle database errors gracefully
    # This scenario tests error handling for invalid operations
    When I execute the query "SELECT * FROM non_existent_table"
    # The step definitions should handle SQL exceptions gracefully
    
  @performance @load
  Scenario: Performance test with bulk operations
    # Insert multiple records to test performance
    When I insert data into table "test_performance" with values:
      | id     | 1               |
      | data1  | performance_test_1 |
      | data2  | bulk_insert_test   |
      | value  | 100             |
    # Repeat similar inserts for performance testing
    # This would typically be done in a loop in real scenarios

  @cleanup
  Scenario: Clean up all test data
    When I truncate table "orders"
    And I truncate table "users" 
    And I truncate table "test_performance"
    Then I count records in table "users"
    And I should get count 0
    And I count records in table "orders"
    And I should get count 0