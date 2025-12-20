package org.example.util;

import jakarta.servlet.http.HttpSession;
import org.example.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import static org.example.util.SessionHelper.isAdmin;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionHelperTest {

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserId_ReturnsUserIdFromSession() {
        Long expectedUserId = 123L;
        when(session.getAttribute("userId")).thenReturn(expectedUserId);

        Long result = SessionHelper.getUserId(session);

        assertEquals(expectedUserId, result);
    }

    @Test
    void testGetUserId_ReturnsNullWhenSessionIsNull() {
        Long result = SessionHelper.getUserId(null);
        assertNull(result);
    }

    @Test
    void testGetUserRole_ReturnsRoleFromSession() {
        when(session.getAttribute("userRole")).thenReturn(UserRole.ADMIN);

        UserRole result = SessionHelper.getUserRole(session);

        assertEquals(UserRole.ADMIN, result);
    }

    @Test
    void testIsAdmin_ReturnsFalseForUserRole() {
        when(session.getAttribute("userRole")).thenReturn(UserRole.USER);

        boolean result = isAdmin(session);

        assertFalse(result);
    }

    @Test
    void testIsAdmin_ReturnsFalseForNullRole() {
        when(session.getAttribute("userRole")).thenReturn(null);

        boolean result = isAdmin(session);

        assertFalse(result);
    }

    @Test
    void testIsAdmin_ReturnsFalseForNullSession() {
        boolean result = isAdmin(null);

        assertFalse(result);
    }

    @Test
    void testValidateSession_ReturnsNullForValidSession() {
        when(session.getAttribute("userId")).thenReturn(123L);

        var result = SessionHelper.validateSession(session);

        assertNull(result);
    }

    @Test
    void testValidateSession_Returns401ForInvalidSession() {
        when(session.getAttribute("userId")).thenReturn(null);

        var result = SessionHelper.validateSession(session);

        assertNotNull(result);
        assertEquals(401, result.getStatusCode().value());
    }
}