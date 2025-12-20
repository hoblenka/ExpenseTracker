# User Authentication System

## Overview
The ExpenseTracker now includes a simple session-based authentication system that protects all expense management routes.

## Features
- Login/logout functionality
- Session-based authentication
- Route protection via servlet filter
- Bootstrap-styled login page
- Logout buttons on all pages

## Database Schema
```sql
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Demo user
INSERT IGNORE INTO users (username, password) VALUES ('admin', 'admin');
```

## Demo Credentials
- **Username:** admin
- **Password:** admin

## Components

### Models
- `User.java` - User entity with id, username, password

### Data Access
- `UserDAO.java` - Interface for user data operations
- `UserDAOImpl.java` - JDBC implementation for user operations

### Services
- `AuthService.java` - Authentication logic and user management

### Controllers
- `AuthController.java` - Handles login/logout requests

### Security
- `AuthFilter.java` - Protects routes, redirects unauthenticated users
- `WebConfig.java` - Registers the authentication filter

### Views
- `login.jsp` - Bootstrap-styled login form

## Usage

1. **Access the application:** Navigate to any expense route
2. **Automatic redirect:** Unauthenticated users are redirected to `/login`
3. **Login:** Use demo credentials (admin/admin)
4. **Access granted:** Successful login redirects to `/expenses`
5. **Logout:** Click logout button on any page to end session

## Security Notes
- Passwords are stored in plain text (demo only)
- Session-based authentication
- All routes except `/login` are protected
- Session invalidation on logout

## Testing
Run `AuthServiceTest.java` to verify authentication logic.