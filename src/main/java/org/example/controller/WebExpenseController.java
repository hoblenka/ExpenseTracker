package org.example.controller;

import org.example.service.ExpenseService;
import org.example.model.Expense;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
/*
JSP views will resolve correctly when you run the application.
IntelliJ just can't validate JSP paths at compile time in Spring Boot projects.
*/
public class WebExpenseController {
    
    private final ExpenseService expenseService;

    public WebExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/expenses")
    public String listExpenses(Model model) {
        List<Expense> expenses = expenseService.getAllExpenses();
        BigDecimal totalAmount = expenseService.getTotalAmount();
        model.addAttribute("expenses", expenses);
        model.addAttribute("totalAmount", totalAmount);
        return "list";
    }

    @GetMapping("/expenses/add")
    public String showAddForm() {
        return "add";
    }

    @PostMapping("/expenses/add")
    public String addExpense(@RequestParam String description,
                           @RequestParam BigDecimal amount,
                           @RequestParam String category,
                           @RequestParam String date,
                           Model model) {
        try {
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDate(LocalDate.parse(date));
            
            expenseService.saveExpense(expense);
            return "redirect:/expenses";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("description", description);
            model.addAttribute("amount", amount);
            model.addAttribute("category", category);
            model.addAttribute("date", date);
            return "add";
        }
    }

    @GetMapping("/expenses/edit")
    public String showEditForm(@RequestParam Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id);
        model.addAttribute("expense", expense);
        return "edit";
    }

    @PostMapping("/expenses/edit")
    public String updateExpense(@RequestParam Long id,
                              @RequestParam String description,
                              @RequestParam BigDecimal amount,
                              @RequestParam String category,
                              @RequestParam String date,
                              Model model) {
        try {
            Expense expense = new Expense();
            expense.setId(id);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setCategory(category);
            expense.setDate(LocalDate.parse(date));
            
            expenseService.updateExpense(expense);
            return "redirect:/expenses";
        } catch (IllegalArgumentException e) {
            Expense expense = expenseService.getExpenseById(id);
            model.addAttribute("expense", expense);
            model.addAttribute("error", e.getMessage());
            return "edit";
        }
    }

    @GetMapping("/expenses/delete")
    public String deleteExpense(@RequestParam Long id) {
        expenseService.deleteExpense(id);
        return "redirect:/expenses";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/deleteAll")
    public String deleteAllExpenses() {
        expenseService.deleteAllExpenses();
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/addRandom")
    public String addRandomExpense() {
        expenseService.addRandomExpense();
        return "redirect:/expenses";
    }
}