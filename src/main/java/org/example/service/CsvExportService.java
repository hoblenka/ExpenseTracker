package org.example.service;

import org.example.model.Expense;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CsvExportService {
    
    private final ExpenseCrudService crudService;
    private final ExpenseFilterService filterService;
    
    public CsvExportService(ExpenseCrudService crudService, ExpenseFilterService filterService) {
        this.crudService = crudService;
        this.filterService = filterService;
    }
    
    public String exportAllToCsv() {
        return exportToCsv(crudService.getAllExpenses());
    }
    
    public String exportAllToCsvForUser(Long userId) {
        return exportToCsv(crudService.getAllExpensesByUserId(userId));
    }
    
    public String exportFilteredToCsv(LocalDate startDate, LocalDate endDate, String category) {
        List<Expense> expenses = filterService.getFilteredExpenses(startDate, endDate, category);
        return exportToCsv(expenses);
    }
    
    public String exportFilteredToCsvForUser(LocalDate startDate, LocalDate endDate, String category, Long userId) {
        List<Expense> expenses = filterService.getFilteredExpensesByUserId(startDate, endDate, category, userId);
        return exportToCsv(expenses);
    }

    public String exportToCsv(List<Expense> expenses) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Description,Amount,Category,Date\n");
        
        for (Expense expense : expenses) {
            csv.append(expense.getId()).append(",")
               .append(expense.getDescription()).append(",")
               .append(expense.getAmount()).append(",")
               .append(expense.getCategory().getDisplayName()).append(",")
               .append(expense.getDate()).append("\n");
        }
        
        return csv.toString();
    }
}