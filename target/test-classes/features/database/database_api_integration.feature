@database @api-integration
Feature: Database API Integration Tests
  As a test automation engineer
  I want to combine database operations with API testing
  So that I can verify end-to-end data flow

  Background:
    Given I have a database connection

  @data-setup @positive
  Scenario: Setup test data for API integration
    When I insert data into table "api_users" with values:
      | user_id     | 2001                    |
      | username    | testuser1               |
      | email       | testuser1@api.com       |
      | password    | encrypted_password_123  |
      | created_date| 2024-01-15              |
      | is_active   | true                    |
    And I insert data into table "api_users" with values:
      | user_id     | 2002                    |
      | username    | testuser2               |
      | email       | testuser2@api.com       |
      | password    | encrypted_password_456  |
      | created_date| 2024-01-16              |
      | is_active   | false                   |
    Then 2 records should be affected

  @data-verification @positive
  Scenario: Verify API user data before API calls
    When I execute the query "SELECT * FROM api_users WHERE is_active = true"
    Then I should get 1 results
    And the results should contain:
      | user_id | username  | email             | is_active |
      | 2001    | testuser1 | testuser1@api.com | true      |
    
    When I count records in table "api_users" where "is_active = true"
    Then I should get count 1

  @audit-trail @positive  
  Scenario: Create audit trail for API operations
    # Simulate API operation logging
    When I insert data into table "api_audit_log" with values:
      | log_id      | 3001                |
      | user_id     | 2001                |
      | operation   | user_login          |
      | endpoint    | /api/auth/login     |
      | timestamp   | 2024-01-15 10:30:00 |
      | status_code | 200                 |
      | response_time| 150                |
    And I insert data into table "api_audit_log" with values:
      | log_id      | 3002                |
      | user_id     | 2001                |
      | operation   | get_user_profile    |
      | endpoint    | /api/users/2001     |
      | timestamp   | 2024-01-15 10:31:00 |
      | status_code | 200                 |
      | response_time| 75                 |
    Then I count records in table "api_audit_log" where "user_id = 2001"
    And I should get count 2

  @session-management @positive
  Scenario: Manage user sessions in database
    # Create user session records
    When I insert data into table "user_sessions" with values:
      | session_id  | sess_123456789      |
      | user_id     | 2001                |
      | created_at  | 2024-01-15 10:30:00 |
      | expires_at  | 2024-01-15 18:30:00 |
      | is_active   | true                |
      | ip_address  | 192.168.1.100       |
    
    # Verify session exists
    When I check if record exists in table "user_sessions" where "session_id = 'sess_123456789' AND is_active = true"
    Then the record should exist
    
    # Simulate session expiry
    When I update table "user_sessions" set "is_active = false" where "session_id = 'sess_123456789'"
    Then 1 records should be affected
    
    # Verify session is expired
    When I check if record exists in table "user_sessions" where "session_id = 'sess_123456789' AND is_active = true"
    Then the record should not exist

  @token-validation @positive
  Scenario: Validate API tokens in database
    # Store API tokens
    When I insert data into table "api_tokens" with values:
      | token_id    | tok_987654321       |
      | user_id     | 2001                |
      | token_hash  | abc123def456ghi789  |
      | created_at  | 2024-01-15 10:30:00 |
      | expires_at  | 2024-01-22 10:30:00 |
      | scope       | read,write          |
      | is_revoked  | false               |
    
    # Validate token exists and is valid
    When I execute the query "SELECT * FROM api_tokens WHERE token_id = 'tok_987654321' AND is_revoked = false"
    Then I should get 1 results
    And the results should contain:
      | token_id      | user_id | scope      | is_revoked |
      | tok_987654321 | 2001    | read,write | false      |

  @rate-limiting @positive
  Scenario: Track API rate limiting data
    # Insert rate limiting records
    When I insert data into table "rate_limit_log" with values:
      | user_id     | 2001                |
      | endpoint    | /api/users          |
      | request_time| 2024-01-15 10:30:01 |
      | count       | 1                   |
    And I insert data into table "rate_limit_log" with values:
      | user_id     | 2001                |
      | endpoint    | /api/users          |
      | request_time| 2024-01-15 10:30:02 |
      | count       | 2                   |
    And I insert data into table "rate_limit_log" with values:
      | user_id     | 2001                |
      | endpoint    | /api/users          |
      | request_time| 2024-01-15 10:30:03 |
      | count       | 3                   |
    
    # Count requests in time window
    When I count records in table "rate_limit_log" where "user_id = 2001 AND endpoint = '/api/users'"
    Then I should get count 3
    
    # Get max count for rate limiting check
    When I execute the query "SELECT MAX(count) as max_requests FROM rate_limit_log WHERE user_id = 2001 AND endpoint = '/api/users'"
    Then I should get 1 results
    And the results should contain:
      | max_requests |
      | 3            |

  @data-synchronization @positive
  Scenario: Test data synchronization between systems
    # Simulate external system data
    When I insert data into table "external_sync" with values:
      | sync_id     | sync_001            |
      | user_id     | 2001                |
      | external_id | ext_user_123        |
      | sync_status | pending             |
      | last_sync   | 2024-01-15 09:00:00 |
      | data_hash   | hash_abc123         |
    
    # Update sync status
    When I update table "external_sync" set "sync_status = 'completed', last_sync = '2024-01-15 10:30:00'" where "sync_id = 'sync_001'"
    Then 1 records should be affected
    
    # Verify sync completion
    When I validate data in table "external_sync" where "sync_id = 'sync_001'" has values:
      | sync_status | completed           |
      | user_id     | 2001                |
      | external_id | ext_user_123        |

  @error-logging @positive
  Scenario: Log and track API errors
    # Insert error logs
    When I insert data into table "error_log" with values:
      | error_id    | err_001             |
      | user_id     | 2002                |
      | endpoint    | /api/auth/login     |
      | error_code  | AUTH_FAILED         |
      | error_message| Invalid credentials |
      | timestamp   | 2024-01-15 10:35:00 |
      | severity    | warning             |
    
    # Count errors by user
    When I count records in table "error_log" where "user_id = 2002"
    Then I should get count 1
    
    # Count errors by severity
    When I count records in table "error_log" where "severity = 'warning'"
    Then I should get count 1

  @cleanup
  Scenario: Clean up integration test data
    When I truncate table "api_users"
    And I truncate table "api_audit_log"
    And I truncate table "user_sessions"
    And I truncate table "api_tokens"
    And I truncate table "rate_limit_log"
    And I truncate table "external_sync"
    And I truncate table "error_log"
    Then I count records in table "api_users"
    And I should get count 0