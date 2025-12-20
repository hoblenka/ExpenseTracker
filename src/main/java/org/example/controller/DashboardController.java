package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.service.DashboardService;
import org.example.util.SessionHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Map;

@Controller
public class DashboardController {
    
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("totalSpending", dashboardService.getTotalSpendingForUser(userId));
        model.addAttribute("totalExpenses", dashboardService.getTotalExpenseCountForUser(userId));
        return "dashboard";
    }

    @GetMapping("/api/dashboard/category-data")
    @ResponseBody
    public Map<String, BigDecimal> getCategoryData(HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return Map.of();
        }
        return dashboardService.getSpendingByCategoryForUser(userId);
    }

    @GetMapping("/api/dashboard/monthly-data")
    @ResponseBody
    public Map<String, BigDecimal> getMonthlyData(HttpSession session) {
        Long userId = SessionHelper.getUserId(session);
        if (userId == null) {
            return Map.of();
        }
        return dashboardService.getSpendingByMonthForUser(userId);
    }
}