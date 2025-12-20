-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Create expenses table with composite primary key (id, user_id)
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT NOT NULL,
    user_id INT NOT NULL DEFAULT 1,
    PRIMARY KEY (id, user_id),
    CONSTRAINT fk_expenses_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert demo users
INSERT IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'ADMIN');
INSERT IGNORE INTO users (username, password, role) VALUES ('user1', 'user1', 'USER');
INSERT IGNORE INTO users (username, password, role) VALUES ('user2', 'user2', 'USER');