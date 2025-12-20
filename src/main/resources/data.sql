-- Insert demo data only if tables are empty
INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin');
INSERT IGNORE INTO users (username, password) VALUES ('user1', 'user1');