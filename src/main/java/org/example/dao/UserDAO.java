package org.example.dao;

import org.example.model.User;

import java.util.List;

public interface UserDAO {
    User findByUsername(String username);
    User save(User user);
    List<User> findAll();
}