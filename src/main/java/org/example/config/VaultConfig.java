package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vault.db")
public class VaultConfig {
    private String username;
    private String password;
    
    public String getUsername() { 
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("Username is not configured");
        }
        return username; 
    }
    
    public void setUsername(String username) { 
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username; 
    }
    
    String getPasswordForDataSource() {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalStateException("Password is not configured");
        }
        return password; 
    }
    
    public void setPassword(String password) { 
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password; 
    }
}
