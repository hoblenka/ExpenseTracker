package org.example;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ExpenseTrackerTest {

    @Autowired
    private ExpenseDAO expenseDAO;

    @Test
    public void testAddExpense() {
        Expense expense = new Expense("Test expense", new BigDecimal("100.00"), "Food", LocalDate.now());
        expenseDAO.save(expense);
        
        List<Expense> expenses = expenseDAO.findAll();
        assertFalse(expenses.isEmpty());
    }

    @Test
    public void testListExpenses() {
        List<Expense> expenses = expenseDAO.findAll();
        assertNotNull(expenses);
    }

    @Test
    public void testEditExpense() {
        Expense expense = new Expense("Original", new BigDecimal("50.00"), "Transport", LocalDate.now());
        expenseDAO.save(expense);
        
        List<Expense> expenses = expenseDAO.findAll();
        Expense saved = expenses.get(expenses.size() - 1);
        
        saved.setDescription("Updated");
        expenseDAO.update(saved);
        
        Expense updated = expenseDAO.findById(saved.getId());
        assertEquals("Updated", updated.getDescription());
    }

    @Test
    public void testDeleteExpense() {
        Expense expense = new Expense("To delete", new BigDecimal("25.00"), "Other", LocalDate.now());
        expenseDAO.save(expense);
        
        List<Expense> expenses = expenseDAO.findAll();
        Expense saved = expenses.get(expenses.size() - 1);
        
        expenseDAO.deleteById(saved.getId());
        
        Expense deleted = expenseDAO.findById(saved.getId());
        assertNull(deleted);
    }

    @Test
    public void testDeleteAllExpenses() {
        // Add some test expenses
        expenseDAO.save(new Expense("Test 1", new BigDecimal("10.00"), "Food", LocalDate.now()));
        expenseDAO.save(new Expense("Test 2", new BigDecimal("20.00"), "Transport", LocalDate.now()));
        
        // Verify expenses exist
        List<Expense> expensesBefore = expenseDAO.findAll();
        assertTrue(expensesBefore.size() >= 2);
        
        // Delete all
        expenseDAO.deleteAll();
        
        // Verify all deleted
        List<Expense> expensesAfter = expenseDAO.findAll();
        assertTrue(expensesAfter.isEmpty());
    }

    @Test
    public void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> new Expense("Test", new BigDecimal("10.00"), "InvalidCategory", LocalDate.now()));
    }

    @Test
    public void testValidCategories() {
        // Test all valid categories
        String[] validCategories = {"Food", "Transport", "Utilities", "Entertainment", "Shopping", "Rent", "Other"};
        
        for (String category : validCategories) {
            Expense expense = new Expense("Test", new BigDecimal("10.00"), category, LocalDate.now());
            assertEquals(category, expense.getCategory());
        }
    }

    @Test
    public void testTotalAmount() {
        // Clear existing data
        expenseDAO.deleteAll();
        
        // Add test expenses
        expenseDAO.save(new Expense("Test 1", new BigDecimal("10.50"), "Food", LocalDate.now()));
        expenseDAO.save(new Expense("Test 2", new BigDecimal("25.75"), "Transport", LocalDate.now()));
        expenseDAO.save(new Expense("Test 3", new BigDecimal("5.25"), "Other", LocalDate.now()));
        
        // Calculate expected total
        BigDecimal expectedTotal = new BigDecimal("41.50");
        
        // Get actual total from service
        ExpenseService service = new ExpenseService(expenseDAO);
        BigDecimal actualTotal = service.getTotalAmount();
        
        assertEquals(0, expectedTotal.compareTo(actualTotal));
    }
}