-- Sample Test Data for Database Testing
-- This script populates the database with sample data for testing purposes

-- ============================================================================
-- SAMPLE USERS DATA
-- ============================================================================

INSERT INTO users (id, name, email, status, age) VALUES 
(100, 'Alice Johnson', 'alice.johnson@test.com', 'active', 28),
(101, 'Bob Smith', 'bob.smith@test.com', 'active', 35),
(102, 'Charlie Brown', 'charlie.brown@test.com', 'inactive', 42),
(103, 'Diana Prince', 'diana.prince@test.com', 'active', 31),
(104, 'Eve Wilson', 'eve.wilson@test.com', 'pending', 26);

-- ============================================================================
-- SAMPLE API USERS DATA
-- ============================================================================

INSERT INTO api_users (user_id, username, email, password, created_date, is_active) VALUES
(1001, 'api_user_1', 'apiuser1@test.com', 'hashed_password_123', '2024-01-01', true),
(1002, 'api_user_2', 'apiuser2@test.com', 'hashed_password_456', '2024-01-02', true),
(1003, 'api_user_3', 'apiuser3@test.com', 'hashed_password_789', '2024-01-03', false),
(1004, 'test_admin', 'admin@test.com', 'admin_password_hash', '2024-01-01', true),
(1005, 'test_viewer', 'viewer@test.com', 'viewer_password_hash', '2024-01-05', true);

-- ============================================================================
-- SAMPLE ORDERS DATA
-- ============================================================================

INSERT INTO orders (order_id, user_id, product, amount, status, order_date) VALUES
(5001, 100, 'Laptop Dell XPS 13', 1299.99, 'completed', '2024-01-15 10:30:00'),
(5002, 100, 'Wireless Mouse', 29.99, 'completed', '2024-01-16 14:20:00'),
(5003, 101, 'Monitor 27 inch', 299.99, 'pending', '2024-01-17 09:15:00'),
(5004, 103, 'Keyboard Mechanical', 149.99, 'shipped', '2024-01-18 11:45:00'),
(5005, 104, 'Webcam HD', 79.99, 'cancelled', '2024-01-19 16:30:00');

-- ============================================================================
-- SAMPLE ORDER ITEMS DATA
-- ============================================================================

INSERT INTO order_items (item_id, order_id, product_name, quantity, unit_price, total_price) VALUES
(6001, 5001, 'Laptop Dell XPS 13', 1, 1299.99, 1299.99),
(6002, 5002, 'Wireless Mouse', 1, 29.99, 29.99),
(6003, 5003, 'Monitor 27 inch', 1, 299.99, 299.99),
(6004, 5004, 'Keyboard Mechanical', 1, 149.99, 149.99),
(6005, 5004, 'Key Caps Set', 1, 25.00, 25.00),
(6006, 5005, 'Webcam HD', 1, 79.99, 79.99);

-- ============================================================================
-- SAMPLE API TOKENS DATA
-- ============================================================================

INSERT INTO api_tokens (token_id, user_id, token_hash, expires_at, scope, is_revoked) VALUES
('token_001', 1001, 'hash_abc123def456', '2024-12-31 23:59:59', 'read,write', false),
('token_002', 1002, 'hash_ghi789jkl012', '2024-12-31 23:59:59', 'read', false),
('token_003', 1004, 'hash_mno345pqr678', '2024-12-31 23:59:59', 'admin', false),
('token_004', 1001, 'hash_stu901vwx234', '2024-06-30 23:59:59', 'read,write', true),
('token_005', 1005, 'hash_yza567bcd890', '2024-12-31 23:59:59', 'read', false);

-- ============================================================================
-- SAMPLE USER SESSIONS DATA
-- ============================================================================

INSERT INTO user_sessions (session_id, user_id, created_at, expires_at, is_active, ip_address) VALUES
('sess_001_active', 1001, '2024-01-20 08:00:00', '2024-01-20 20:00:00', true, '192.168.1.101'),
('sess_002_active', 1002, '2024-01-20 09:00:00', '2024-01-20 21:00:00', true, '192.168.1.102'),
('sess_003_expired', 1003, '2024-01-19 10:00:00', '2024-01-19 22:00:00', false, '192.168.1.103'),
('sess_004_active', 1004, '2024-01-20 07:30:00', '2024-01-20 19:30:00', true, '192.168.1.104'),
('sess_005_expired', 1001, '2024-01-19 12:00:00', '2024-01-20 00:00:00', false, '192.168.1.101');

-- ============================================================================
-- SAMPLE API AUDIT LOG DATA
-- ============================================================================

INSERT INTO api_audit_log (log_id, user_id, operation, endpoint, timestamp, status_code, response_time) VALUES
(7001, 1001, 'login', '/api/auth/login', '2024-01-20 08:00:01', 200, 150),
(7002, 1001, 'get_profile', '/api/users/1001', '2024-01-20 08:00:30', 200, 75),
(7003, 1002, 'login', '/api/auth/login', '2024-01-20 09:00:01', 200, 145),
(7004, 1003, 'login', '/api/auth/login', '2024-01-20 10:00:01', 401, 50),
(7005, 1001, 'update_profile', '/api/users/1001', '2024-01-20 08:15:00', 200, 120),
(7006, 1004, 'get_users', '/api/admin/users', '2024-01-20 07:30:15', 200, 200),
(7007, 1002, 'get_orders', '/api/orders', '2024-01-20 09:30:00', 200, 180),
(7008, 1005, 'login', '/api/auth/login', '2024-01-20 11:00:01', 200, 155);

-- ============================================================================
-- SAMPLE RATE LIMIT LOG DATA
-- ============================================================================

INSERT INTO rate_limit_log (user_id, endpoint, request_time, count) VALUES
(1001, '/api/users/1001', '2024-01-20 08:00:30', 1),
(1001, '/api/users/1001', '2024-01-20 08:01:00', 2),
(1001, '/api/users/1001', '2024-01-20 08:01:30', 3),
(1002, '/api/orders', '2024-01-20 09:30:00', 1),
(1002, '/api/orders', '2024-01-20 09:30:15', 2),
(1004, '/api/admin/users', '2024-01-20 07:30:15', 1),
(1004, '/api/admin/users', '2024-01-20 07:30:45', 2),
(1004, '/api/admin/users', '2024-01-20 07:31:15', 3);

-- ============================================================================
-- SAMPLE EXTERNAL SYNC DATA
-- ============================================================================

INSERT INTO external_sync (sync_id, user_id, external_id, sync_status, last_sync, data_hash) VALUES
('sync_001', 1001, 'ext_user_001', 'completed', '2024-01-20 06:00:00', 'hash_user_001_v1'),
('sync_002', 1002, 'ext_user_002', 'completed', '2024-01-20 06:05:00', 'hash_user_002_v1'),
('sync_003', 1003, 'ext_user_003', 'failed', '2024-01-20 06:10:00', 'hash_user_003_v1'),
('sync_004', 1004, 'ext_user_004', 'pending', '2024-01-20 06:15:00', 'hash_user_004_v1'),
('sync_005', 1005, 'ext_user_005', 'in_progress', '2024-01-20 06:20:00', 'hash_user_005_v1');

-- ============================================================================
-- SAMPLE ERROR LOG DATA
-- ============================================================================

INSERT INTO error_log (error_id, user_id, endpoint, error_code, error_message, timestamp, severity) VALUES
('err_001', 1003, '/api/auth/login', 'AUTH_FAILED', 'Invalid credentials provided', '2024-01-20 10:00:01', 'warning'),
('err_002', null, '/api/health', 'DB_CONNECTION', 'Database connection timeout', '2024-01-20 05:30:00', 'error'),
('err_003', 1001, '/api/users/1001', 'VALIDATION_ERROR', 'Invalid email format', '2024-01-20 08:20:00', 'warning'),
('err_004', null, '/api/orders', 'INTERNAL_ERROR', 'Null pointer exception in order processing', '2024-01-20 12:00:00', 'critical'),
('err_005', 1002, '/api/orders/create', 'BUSINESS_RULE', 'Order amount exceeds daily limit', '2024-01-20 14:30:00', 'info');

-- ============================================================================
-- SAMPLE PERFORMANCE TEST DATA
-- ============================================================================

INSERT INTO test_performance (id, data1, data2, value) VALUES
(1, 'performance_test_1', 'scenario_A', 100),
(2, 'performance_test_2', 'scenario_B', 250),
(3, 'performance_test_3', 'scenario_C', 150),
(4, 'load_test_1', 'concurrent_users_50', 300),
(5, 'load_test_2', 'concurrent_users_100', 450),
(6, 'stress_test_1', 'peak_load', 800),
(7, 'endurance_test_1', 'long_run_8h', 200),
(8, 'spike_test_1', 'sudden_load', 600);

-- ============================================================================
-- SAMPLE APP CONFIG DATA
-- ============================================================================

INSERT INTO app_config (config_key, config_value, description, is_active) VALUES
('api_rate_limit', '100', 'Maximum API calls per minute per user', true),
('session_timeout', '3600', 'Session timeout in seconds', true),
('max_file_upload_size', '10485760', 'Maximum file upload size in bytes (10MB)', true),
('enable_debug_logging', 'false', 'Enable debug level logging', true),
('maintenance_mode', 'false', 'Application maintenance mode flag', true),
('email_notifications', 'true', 'Enable email notifications', true),
('database_pool_size', '20', 'Database connection pool size', true),
('cache_expiry_time', '1800', 'Cache expiry time in seconds', true);

-- ============================================================================
-- COMMIT TRANSACTION
-- ============================================================================

COMMIT;

-- Print completion message
SELECT 'Sample test data inserted successfully!' as status,
       (SELECT COUNT(*) FROM users) as users_count,
       (SELECT COUNT(*) FROM api_users) as api_users_count,
       (SELECT COUNT(*) FROM orders) as orders_count,
       (SELECT COUNT(*) FROM api_tokens) as tokens_count;