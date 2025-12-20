package org.example.util;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

public class SessionHelper {
    
    public static Long getUserId(HttpSession session) {
        return session != null ? (Long) session.getAttribute("userId") : null;
    }
    
    public static <T> ResponseEntity<T> validateSession(HttpSession session) {
        Long userId = getUserId(session);
        return userId == null ? ResponseEntity.status(401).build() : null;
    }
}