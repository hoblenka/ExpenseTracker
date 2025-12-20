package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    void testIsAdmin_ReturnsTrueForAdminRole() {
        User user = new User("admin", "password");
        user.setRole(UserRole.valueOf("ADMIN"));

        assertTrue(user.isAdmin());
    }

    @Test
    void testIsAdmin_ReturnsFalseForUserRole() {
        User user = new User("user1", "password");
        user.setRole(UserRole.valueOf("USER"));

        assertFalse(user.isAdmin());
    }

    @Test
    void testIsAdmin_ReturnsFalseForNullRole() {
        User user = new User("user2", "password");
        user.setRole(null);

        assertFalse(user.isAdmin());
    }

    @Test
    void testDefaultRole_IsUser() {
        User user = new User("user3", "password");

        assertEquals(UserRole.USER, user.getRole());
        assertFalse(user.isAdmin());
    }

    @Test
    void testSetRole_UpdatesRole() {
        User user = new User("user4", "password");
        assertEquals(UserRole.USER, user.getRole());

        user.setRole(UserRole.valueOf("ADMIN"));
        assertEquals(UserRole.ADMIN, user.getRole());
        assertTrue(user.isAdmin());
    }
}