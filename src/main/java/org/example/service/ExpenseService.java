package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    private final ExpenseDAO expenseDAO;

    public ExpenseService(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public List<Expense> getAllExpenses() {
        return expenseDAO.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseDAO.findById(id);
    }

    public void saveExpense(Expense expense) {
        expenseDAO.save(expense);
    }

    public void updateExpense(Expense expense) {
        expenseDAO.update(expense);
    }

    public void deleteExpense(Long id) {
        expenseDAO.deleteById(id);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenseDAO.findByCategory(category);
    }

    public void deleteAllExpenses() {
        expenseDAO.deleteAll();
    }

    public BigDecimal getTotalAmount() {
        return getAllExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addRandomExpense() {
        ExpenseCategory[] categories = ExpenseCategory.values();
        String[] descriptions = {
            "Lunch", "Coffee", "Groceries", "Bus ticket", "Taxi", "Gas bill", "Movie ticket", 
            "Shopping", "Dinner", "Breakfast", "Electricity bill", "Water bill", "Rent payment"
        };
        
        java.util.Random random = new java.util.Random();
        ExpenseCategory category = categories[random.nextInt(categories.length)];
        String description = descriptions[random.nextInt(descriptions.length)];
        BigDecimal amount = BigDecimal.valueOf(5 + random.nextDouble() * 95).setScale(2, java.math.RoundingMode.HALF_UP);
        LocalDate date = LocalDate.now().minusDays(random.nextInt(30));
        
        Expense expense = new Expense(description, amount, category, date);
        saveExpense(expense);
    }

    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseDAO.findByDateRange(startDate, endDate);
    }

    public String exportToCsv() {
        return exportFilteredToCsv(null, null, null);
    }

    public String exportFilteredToCsv(LocalDate startDate, LocalDate endDate, String category) {
        List<Expense> expenses = getFilteredExpenses(startDate, endDate, category);
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

    public List<Expense> getFilteredExpenses(LocalDate startDate, LocalDate endDate, String category) {
        List<Expense> expenses = getAllExpenses();
        
        if (startDate != null && endDate != null) {
            expenses = expenses.stream()
                .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        }
        
        if (category != null && !category.trim().isEmpty()) {
            String trimmedCategory = category.trim();
            expenses = expenses.stream()
                .filter(e -> e.getCategoryDisplayName().equalsIgnoreCase(trimmedCategory))
                .collect(Collectors.toList());
        }
        
        return expenses;
    }

    public List<Expense> sortExpenses(List<Expense> expenses, String sortBy) {
        return expenses.stream().sorted((e1, e2) -> switch (sortBy.toLowerCase()) {
            case "id" -> e1.getId().compareTo(e2.getId());
            case "description" -> e1.getDescription().compareToIgnoreCase(e2.getDescription());
            case "amount" -> e1.getAmount().compareTo(e2.getAmount());
            case "category" -> e1.getCategoryDisplayName().compareToIgnoreCase(e2.getCategoryDisplayName());
            case "date" -> e1.getDate().compareTo(e2.getDate());
            default -> 0;
        }).collect(Collectors.toList());
    }
}