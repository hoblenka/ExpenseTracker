package org.example.service;

import org.example.model.Expense;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseFilterService {
    
    private final ExpenseCrudService expenseCrudService;
    
    public ExpenseFilterService(ExpenseCrudService crudService) {
        this.expenseCrudService = crudService;
    }
    
    public List<Expense> getFilteredExpenses(LocalDate startDate, LocalDate endDate, String category) {
        List<Expense> expenses = expenseCrudService.getAllExpenses();
        return filterExpenses(expenses, startDate, endDate, category);
    }
    
    public List<Expense> getFilteredExpensesByUserId(LocalDate startDate, LocalDate endDate, String category, Long userId) {
        List<Expense> expenses = expenseCrudService.getAllExpensesByUserId(userId);
        return filterExpenses(expenses, startDate, endDate, category);
    }

    public List<Expense> filterExpensesByCategory(String category) {
        return expenseCrudService.getAllExpenses().stream()
            .filter(e -> e.getCategoryDisplayName().equalsIgnoreCase(category.trim()))
            .collect(Collectors.toList());
    }
    
    public List<Expense> filterExpensesByCategoryAndUserId(String category, Long userId) {
        return expenseCrudService.getAllExpensesByUserId(userId).stream()
            .filter(e -> e.getCategoryDisplayName().equalsIgnoreCase(category.trim()))
            .collect(Collectors.toList());
    }

    public List<Expense> filterExpensesByDateRangeAndUserId(LocalDate startDate, LocalDate endDate, Long userId) {
        return expenseCrudService.getAllExpensesByUserId(userId).stream()
            .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
            .collect(Collectors.toList());
    }

    public List<Expense> filterExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseCrudService.getAllExpenses().stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Expense> filterExpenses(List<Expense> expenses, LocalDate startDate, LocalDate endDate, String category) {
        List<Expense> filtered = expenses;
        
        if (startDate != null && endDate != null) {
            filtered = filtered.stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        }
        
        if (category != null && !category.trim().isEmpty()) {
            String trimmedCategory = category.trim();
            filtered = filtered.stream()
                .filter(e -> e.getCategoryDisplayName().equalsIgnoreCase(trimmedCategory))
                .collect(Collectors.toList());
        }
        
        return filtered;
    }
}