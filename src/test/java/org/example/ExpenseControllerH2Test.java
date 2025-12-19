package org.example;

import org.example.service.ExpenseService;
import org.example.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseControllerH2Test {

    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private javax.sql.DataSource dataSource;

    @BeforeEach
    void ensureTableExists() {
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS expenses (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "amount DECIMAL(10,2) NOT NULL, " +
                "category VARCHAR(255) NOT NULL, " +
                "date DATE NOT NULL, " +
                "description TEXT)");
        } catch (Exception e) {
            // Table might already exist
        }
    }

    @Test
    void testAddAndRemoveExpense() {
        // Get initial count
        int initialCount = expenseService.getAllExpenses().size();
        
        // Create and add test expense
        Expense testExpense = new Expense("Test Coffee", new BigDecimal("5.50"), "Food", LocalDate.now());
        expenseService.saveExpense(testExpense);
        
        // Verify expense was added
        List<Expense> expenses = expenseService.getAllExpenses();
        assertEquals(initialCount + 1, expenses.size());
        
        // Find the added expense
        Expense addedExpense = expenses.stream()
                .filter(e -> "Test Coffee".equals(e.getDescription()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(addedExpense);
        assertEquals("Test Coffee", addedExpense.getDescription());
        assertEquals(new BigDecimal("5.50"), addedExpense.getAmount());
        assertEquals("Food", addedExpense.getCategory());
        
        // Remove the expense
        expenseService.deleteExpense(addedExpense.getId());
        
        // Verify expense was removed
        List<Expense> finalExpenses = expenseService.getAllExpenses();
        assertEquals(initialCount, finalExpenses.size());
        
        // Verify the specific expense is gone
        boolean expenseExists = finalExpenses.stream()
                .anyMatch(e -> "Test Coffee".equals(e.getDescription()));
        assertFalse(expenseExists);
    }
}