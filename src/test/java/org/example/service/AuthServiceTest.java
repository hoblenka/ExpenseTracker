package org.example.service;

import org.example.dao.UserDAO;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userDAO);
    }

    @Test
    void authenticate_ValidCredentials_ReturnsUser() {
        // Arrange
        User user = new User("admin", "admin");
        user.setId(1L);
        when(userDAO.findByUsername("admin")).thenReturn(user);

        // Act
        User result = authService.authenticate("admin", "admin");

        // Assert
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void authenticate_InvalidPassword_ReturnsNull() {
        // Arrange
        User user = new User("admin", "admin");
        when(userDAO.findByUsername("admin")).thenReturn(user);

        // Act
        User result = authService.authenticate("admin", "wrong");

        // Assert
        assertNull(result);
    }

    @Test
    void authenticate_UserNotFound_ReturnsNull() {
        // Arrange
        when(userDAO.findByUsername("nonexistent")).thenReturn(null);

        // Act
        User result = authService.authenticate("nonexistent", "password");

        // Assert
        assertNull(result);
    }

    @Test
    void createUser_NewUser_ReturnsUser() {
        // Arrange
        when(userDAO.findByUsername("newuser")).thenReturn(null);
        User savedUser = new User("newuser", "password");
        savedUser.setId(2L);
        when(userDAO.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = authService.createUser("newuser", "password");

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(2L, result.getId());
    }

    @Test
    void createUser_ExistingUser_ThrowsException() {
        // Arrange
        User existingUser = new User("admin", "admin");
        when(userDAO.findByUsername("admin")).thenReturn(existingUser);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> authService.createUser("admin", "newpassword"));
    }
}