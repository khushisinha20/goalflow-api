-- ================================================
-- GoalFlow Database Schema
-- ================================================

-- Drop tables if they exist (for clean slate)
DROP TABLE IF EXISTS reflections CASCADE;
DROP TABLE IF EXISTS user_preferences CASCADE;
DROP TABLE IF EXISTS executions CASCADE;
DROP TABLE IF EXISTS targets CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS streaks CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop types if they exist
DROP TYPE IF EXISTS execution_status;

-- ================================================
-- Create Custom Types
-- ================================================

CREATE TYPE execution_status AS ENUM ('PENDING', 'COMPLETED', 'SKIPPED', 'MISSED');

-- ================================================
-- Users Table
-- ================================================

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       timezone VARCHAR(50) DEFAULT 'UTC',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- ================================================
-- Categories Table
-- ================================================

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            name VARCHAR(50) NOT NULL,
                            color VARCHAR(7) DEFAULT '#3B82F6',
                            is_default BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            UNIQUE(user_id, name)
);

CREATE INDEX idx_categories_user_id ON categories(user_id);

-- ================================================
-- Targets Table
-- ================================================

CREATE TABLE targets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         title VARCHAR(100) NOT NULL,
                         description TEXT,
                         target_date DATE NOT NULL,
                         category_id BIGINT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_targets_user_date ON targets(user_id, target_date);
CREATE INDEX idx_targets_user_id ON targets(user_id);
CREATE INDEX idx_targets_date ON targets(target_date);
CREATE INDEX idx_targets_category ON targets(category_id);

-- ================================================
-- Executions Table
-- ================================================

CREATE TABLE executions (
                            id BIGSERIAL PRIMARY KEY,
                            target_id BIGINT NOT NULL UNIQUE,
                            status execution_status DEFAULT 'PENDING',
                            completion_time TIMESTAMP,
                            notes TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (target_id) REFERENCES targets(id) ON DELETE CASCADE
);

CREATE INDEX idx_executions_target_id ON executions(target_id);
CREATE INDEX idx_executions_status ON executions(status);

-- ================================================
-- Streaks Table
-- ================================================

CREATE TABLE streaks (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL UNIQUE,
                         current_streak INT DEFAULT 0,
                         best_streak INT DEFAULT 0,
                         last_completed_date DATE,
                         last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_streaks_user_id ON streaks(user_id);

-- ================================================
-- Reflections Table
-- ================================================

CREATE TABLE reflections (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             month INT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year INT NOT NULL CHECK (year BETWEEN 2020 AND 2100),
    content TEXT NOT NULL,
    completion_rate DECIMAL(5,2),
    targets_completed INT,
    targets_total INT,
    best_streak INT,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE(user_id, month, year)
);

CREATE INDEX idx_reflections_user_month_year ON reflections(user_id, year, month);

-- ================================================
-- User Preferences Table
-- ================================================

CREATE TABLE user_preferences (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL UNIQUE,
                                  reminder_enabled BOOLEAN DEFAULT TRUE,
                                  reminder_time TIME DEFAULT '20:00:00',
                                  weekly_summary_enabled BOOLEAN DEFAULT TRUE,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_preferences_user_id ON user_preferences(user_id);

-- ================================================
-- Insert Default Data (for testing)
-- ================================================

-- Insert a test user (password: "password123" hashed with BCrypt)
INSERT INTO users (email, password_hash, full_name, timezone) VALUES
    ('test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test User', 'UTC');

-- Insert default categories for the test user
INSERT INTO categories (user_id, name, color, is_default) VALUES
                                                              (1, 'Learning', '#3B82F6', TRUE),
                                                              (1, 'Fitness', '#10B981', TRUE),
                                                              (1, 'Work', '#F59E0B', TRUE),
                                                              (1, 'Personal', '#8B5CF6', TRUE);

-- Insert a streak record for the test user
INSERT INTO streaks (user_id, current_streak, best_streak) VALUES
    (1, 0, 0);

-- Insert user preferences for the test user
INSERT INTO user_preferences (user_id) VALUES (1);

-- ================================================
-- Verify Schema
-- ================================================

-- View all tables
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;