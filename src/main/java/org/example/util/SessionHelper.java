package org.example.util;

import jakarta.servlet.http.HttpSession;
import org.example.model.UserRole;
import org.springframework.http.ResponseEntity;

public class SessionHelper {
    
    public static Long getUserId(HttpSession session) {
        return session != null ? (Long) session.getAttribute("userId") : null;
    }

    public static UserRole getUserRole(HttpSession session) {
        if (session == null) return UserRole.USER;
        return (UserRole) session.getAttribute("userRole");
    }


    public static boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        return UserRole.ADMIN.equals(getUserRole(session));
    }
    
    public static <T> ResponseEntity<T> validateSession(HttpSession session) {
        Long userId = getUserId(session);
        return userId == null ? ResponseEntity.status(401).build() : null;
    }
}