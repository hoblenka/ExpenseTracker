package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public User createUser(String username, String password) {
        if (userDAO.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        return userDAO.save(new User(username, password));
    }
}