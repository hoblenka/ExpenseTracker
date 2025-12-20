package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseCrudService {
    
    private final ExpenseDAO expenseDAO;
    private final ExpenseIdService idService;

    public ExpenseCrudService(ExpenseDAO expenseDAO, ExpenseIdService idService) {
        this.expenseDAO = expenseDAO;
        this.idService = idService;
    }

    public List<Expense> getAllExpenses() { return expenseDAO.findAll(); }

    public Expense getExpenseById(Long id) {
        return expenseDAO.findById(id);
    }

    public Expense saveExpense(Expense expense) {
        if (expense.getId() == null) {
            expense.setId(idService.getNextAvailableId());
        }
        expenseDAO.save(expense);
        return expense;
    }

    public void updateExpense(Expense expense) {
        expenseDAO.update(expense);
    }

    public void deleteExpense(Long id) {
        expenseDAO.deleteById(id);
    }

    public void deleteAllExpenses() {
        expenseDAO.deleteAll();
    }

    public BigDecimal getTotalAmount() {
        return getAllExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Expense addRandomExpense() {
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
        return saveExpense(expense);
    }

    public Expense addMultipleRandomExpenses(int count) {
        Expense lastExpense = null;
        for (int i = 0; i < count; i++) {
            lastExpense = addRandomExpense();
        }
        return lastExpense;
    }
}