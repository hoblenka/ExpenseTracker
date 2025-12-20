package org.example.controller;

import org.example.model.User;
import org.example.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLogin() {
        logger.info("Showing login page");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session, Model model) {
        logger.info("Login attempt for username: {}", username);
        
        User user = authService.authenticate(username, password);
        if (user != null) {
            logger.info("Authentication successful for user: {} (ID: {})", username, user.getId());
            session.setAttribute("userId", user.getId());
            return "redirect:/expenses";
        }
        
        logger.warn("Authentication failed for username: {}", username);
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("User logging out");
        session.invalidate();
        return "redirect:/login";
    }
}