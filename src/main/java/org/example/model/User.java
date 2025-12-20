package org.example.model;

public class User {
    private Long id;
    private String username;
    private String password;
    private UserRole role;

    public User() {
        this.role = UserRole.USER;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = UserRole.USER;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    
    public String getRoleDisplayName() { return role != null ? role.getDisplayName() : "USER"; }
    
    public boolean isAdmin() { return role == UserRole.ADMIN; }
}