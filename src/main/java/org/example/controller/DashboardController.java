package org.example.controller;

import org.example.service.DashboardService;
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
    public String dashboard(Model model) {
        model.addAttribute("totalSpending", dashboardService.getTotalSpending());
        model.addAttribute("totalExpenses", dashboardService.getTotalExpenseCount());
        return "dashboard";
    }

    @GetMapping("/api/dashboard/category-data")
    @ResponseBody
    public Map<String, BigDecimal> getCategoryData() {
        return dashboardService.getSpendingByCategory();
    }

    @GetMapping("/api/dashboard/monthly-data")
    @ResponseBody
    public Map<String, BigDecimal> getMonthlyData() {
        return dashboardService.getSpendingByMonth();
    }
}