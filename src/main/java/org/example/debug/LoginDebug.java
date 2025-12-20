package org.example.debug;

import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LoginDebug implements CommandLineRunner {
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private AuthService authService;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== LOGIN DEBUG ===");
        
        // Check if admin user exists
        User adminUser = userDAO.findByUsername("admin");
        if (adminUser != null) {
            System.out.println("Admin user found: " + adminUser.getUsername() + " (ID: " + adminUser.getId() + ")");
        } else {
            System.out.println("Admin user NOT found - creating one");
            try {
                User newAdmin = authService.createUser("admin", "admin");
                System.out.println("Created admin user: " + newAdmin.getUsername() + " (ID: " + newAdmin.getId() + ")");
            } catch (Exception e) {
                System.out.println("Error creating admin user: " + e.getMessage());
            }
        }
        
        // Test authentication
        User authResult = authService.authenticate("admin", "admin");
        if (authResult != null) {
            System.out.println("Authentication SUCCESS for admin");
        } else {
            System.out.println("Authentication FAILED for admin");
        }
        
        System.out.println("=== END DEBUG ===");
    }
}