package org.example.dao;

import org.example.model.User;

public interface UserDAO {
    User findByUsername(String username);
    User save(User user);
}