package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "vault.db")
@Component
public class VaultConfig {
    private String username;
    private String password;
    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        validateNotEmpty(username, "Username cannot be null or empty");
        this.username = username; 
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) { 
        validateNotEmpty(password, "Password cannot be null or empty");
        this.password = password; 
    }
    
    private void validateNotEmpty(String value, String message) {
        try {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalStateException(message);
            }
        } catch (Exception e) {
            throw new IllegalStateException(message + ": " + e.getMessage(), e);
        }
    }
}
