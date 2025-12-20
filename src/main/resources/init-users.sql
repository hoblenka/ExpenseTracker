-- Create users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Insert demo user (admin/admin)
INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin');