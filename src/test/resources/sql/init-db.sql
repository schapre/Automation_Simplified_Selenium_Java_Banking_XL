-- Database Initialization Script for Test Automation Framework
-- This script creates tables and initial data for database testing
-- Compatible with PostgreSQL, MySQL, and H2 databases

-- ============================================================================
-- USER MANAGEMENT TABLES
-- ============================================================================

-- Main users table for basic CRUD operations
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    age INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API users table for integration testing
CREATE TABLE IF NOT EXISTS api_users (
    user_id INTEGER PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_date DATE,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User sessions for session management testing
CREATE TABLE IF NOT EXISTS user_sessions (
    session_id VARCHAR(50) PRIMARY KEY,
    user_id INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT true,
    ip_address VARCHAR(45),
    user_agent TEXT,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE CASCADE
);

-- ============================================================================
-- ORDER MANAGEMENT TABLES
-- ============================================================================

-- Orders table for relationship testing
CREATE TABLE IF NOT EXISTS orders (
    order_id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    product VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Order items for detailed order testing
CREATE TABLE IF NOT EXISTS order_items (
    item_id INTEGER PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- ============================================================================
-- API MANAGEMENT TABLES
-- ============================================================================

-- API tokens for authentication testing
CREATE TABLE IF NOT EXISTS api_tokens (
    token_id VARCHAR(50) PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    scope VARCHAR(200),
    is_revoked BOOLEAN DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE CASCADE
);

-- API audit log for tracking API operations
CREATE TABLE IF NOT EXISTS api_audit_log (
    log_id INTEGER PRIMARY KEY,
    user_id INTEGER,
    operation VARCHAR(100) NOT NULL,
    endpoint VARCHAR(200) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_code INTEGER,
    response_time INTEGER,
    request_data TEXT,
    response_data TEXT,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE SET NULL
);

-- Rate limiting log for API throttling tests
CREATE TABLE IF NOT EXISTS rate_limit_log (
    id INTEGER PRIMARY KEY,
    user_id INTEGER NOT NULL,
    endpoint VARCHAR(200) NOT NULL,
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    count INTEGER DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE CASCADE
);

-- ============================================================================
-- SYSTEM TABLES
-- ============================================================================

-- External synchronization tracking
CREATE TABLE IF NOT EXISTS external_sync (
    sync_id VARCHAR(50) PRIMARY KEY,
    user_id INTEGER,
    external_id VARCHAR(100),
    sync_status VARCHAR(20) DEFAULT 'pending',
    last_sync TIMESTAMP,
    data_hash VARCHAR(255),
    error_message TEXT,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE SET NULL
);

-- Error logging for system monitoring
CREATE TABLE IF NOT EXISTS error_log (
    error_id VARCHAR(50) PRIMARY KEY,
    user_id INTEGER,
    endpoint VARCHAR(200),
    error_code VARCHAR(50),
    error_message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    severity VARCHAR(20) DEFAULT 'error',
    stack_trace TEXT,
    FOREIGN KEY (user_id) REFERENCES api_users(user_id) ON DELETE SET NULL
);

-- Performance testing table
CREATE TABLE IF NOT EXISTS test_performance (
    id INTEGER PRIMARY KEY,
    data1 VARCHAR(100),
    data2 VARCHAR(100),
    value INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Configuration table for application settings
CREATE TABLE IF NOT EXISTS app_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value TEXT NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- INDEXES for Performance
-- ============================================================================

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_age ON users(age);

-- API users indexes
CREATE INDEX IF NOT EXISTS idx_api_users_username ON api_users(username);
CREATE INDEX IF NOT EXISTS idx_api_users_email ON api_users(email);
CREATE INDEX IF NOT EXISTS idx_api_users_active ON api_users(is_active);

-- Orders indexes
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);

-- Session indexes
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_active ON user_sessions(is_active);
CREATE INDEX IF NOT EXISTS idx_sessions_expires ON user_sessions(expires_at);

-- API audit log indexes
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON api_audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_endpoint ON api_audit_log(endpoint);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON api_audit_log(timestamp);

-- Rate limit indexes
CREATE INDEX IF NOT EXISTS idx_rate_limit_user ON rate_limit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_rate_limit_endpoint ON rate_limit_log(endpoint);
CREATE INDEX IF NOT EXISTS idx_rate_limit_time ON rate_limit_log(request_time);

-- Error log indexes
CREATE INDEX IF NOT EXISTS idx_error_user_id ON error_log(user_id);
CREATE INDEX IF NOT EXISTS idx_error_severity ON error_log(severity);
CREATE INDEX IF NOT EXISTS idx_error_timestamp ON error_log(timestamp);

-- ============================================================================
-- SAMPLE VIEWS for Complex Queries
-- ============================================================================

-- User activity view
CREATE OR REPLACE VIEW user_activity_view AS
SELECT 
    u.user_id,
    u.username,
    u.email,
    u.is_active,
    COUNT(DISTINCT s.session_id) as active_sessions,
    COUNT(DISTINCT al.log_id) as api_calls,
    MAX(al.timestamp) as last_activity
FROM api_users u
LEFT JOIN user_sessions s ON u.user_id = s.user_id AND s.is_active = true
LEFT JOIN api_audit_log al ON u.user_id = al.user_id
GROUP BY u.user_id, u.username, u.email, u.is_active;

-- Order summary view
CREATE OR REPLACE VIEW order_summary_view AS
SELECT 
    o.user_id,
    u.name as user_name,
    COUNT(o.order_id) as total_orders,
    SUM(o.amount) as total_amount,
    AVG(o.amount) as average_order_value,
    MAX(o.order_date) as last_order_date
FROM orders o
JOIN users u ON o.user_id = u.id
GROUP BY o.user_id, u.name;

-- Error summary view
CREATE OR REPLACE VIEW error_summary_view AS
SELECT 
    endpoint,
    severity,
    COUNT(*) as error_count,
    MAX(timestamp) as last_error,
    COUNT(DISTINCT user_id) as affected_users
FROM error_log
WHERE timestamp >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY endpoint, severity
ORDER BY error_count DESC;

-- ============================================================================
-- TRIGGERS (if supported by database)
-- ============================================================================

-- Auto-update timestamp trigger for users table (PostgreSQL syntax)
-- CREATE OR REPLACE FUNCTION update_updated_at_column()
-- RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = CURRENT_TIMESTAMP;
--     RETURN NEW;
-- END;
-- $$ language 'plpgsql';

-- CREATE TRIGGER update_users_updated_at 
--     BEFORE UPDATE ON users 
--     FOR EACH ROW 
--     EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- COMMENTS for Documentation
-- ============================================================================

COMMENT ON TABLE users IS 'Main users table for basic CRUD testing operations';
COMMENT ON TABLE api_users IS 'API users for authentication and integration testing';
COMMENT ON TABLE orders IS 'Orders table for testing relationships and joins';
COMMENT ON TABLE user_sessions IS 'User session management for authentication testing';
COMMENT ON TABLE api_tokens IS 'API token storage for authentication testing';
COMMENT ON TABLE api_audit_log IS 'Audit trail for API operations and monitoring';
COMMENT ON TABLE rate_limit_log IS 'Rate limiting data for API throttling tests';
COMMENT ON TABLE external_sync IS 'External system synchronization tracking';
COMMENT ON TABLE error_log IS 'System error logging and monitoring';
COMMENT ON TABLE test_performance IS 'Performance testing data storage';

-- ============================================================================
-- END OF SCRIPT
-- ============================================================================

COMMIT;

-- Print completion message
SELECT 'Database initialization completed successfully!' as status;