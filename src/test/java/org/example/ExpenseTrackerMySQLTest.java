package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseTrackerMySQLTest {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private ExpenseCrudService expenseCrudService;

    @Test
    void testDatabaseConnectionActive() {
        // Simply verify we can read from database without adding data
        assertDoesNotThrow(() -> {
            List<Expense> expenses = expenseCrudService.getAllExpensesByUserId(1L);
            assertNotNull(expenses);
        });
    }
    
    @Test
    void testRealDatabaseConnection() {
        // Save expense to real MySQL database
        Expense expense = new Expense("MySQL Database Test", new BigDecimal("10.00"), ExpenseCategory.OTHER, LocalDate.now());
        expense.setUserId(1L); // Set default user for testing
        expenseCrudService.saveExpense(expense);
        
        // Retrieve from database through service
        List<Expense> result = expenseCrudService.getAllExpensesByUserId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> "MySQL Database Test".equals(e.getDescription())));
        
        // Cleanup - delete test data
        result.stream()
            .filter(e -> "MySQL Database Test".equals(e.getDescription()))
            .forEach(e -> expenseCrudService.deleteExpenseByIdAndUserId(e.getId(), 1L));
    }

    @Test
    void testDatabasePersistence() {
        // Count existing test expenses before adding new ones
        long initialTestCount = expenseCrudService.getAllExpensesByUserId(1L).stream()
                .filter(e -> e.getDescription().startsWith("MySQL Integration"))
                .count();
        
        Expense expense1 = new Expense("MySQL Integration 1", new BigDecimal("25.50"), ExpenseCategory.OTHER, LocalDate.now());
        expense1.setUserId(1L);
        Expense expense2 = new Expense("MySQL Integration 2", new BigDecimal("15.75"), ExpenseCategory.OTHER, LocalDate.now());
        expense2.setUserId(1L);
        
        expenseCrudService.saveExpense(expense1);
        expenseCrudService.saveExpense(expense2);
        
        // Verify our specific test expenses were added
        List<Expense> allExpenses = expenseCrudService.getAllExpensesByUserId(1L);
        long finalTestCount = allExpenses.stream()
                .filter(e -> e.getDescription().startsWith("MySQL Integration"))
                .count();
        
        assertEquals(initialTestCount + 2, finalTestCount);
        
        // Verify both specific expenses exist
        assertTrue(allExpenses.stream().anyMatch(e -> "MySQL Integration 1".equals(e.getDescription())));
        assertTrue(allExpenses.stream().anyMatch(e -> "MySQL Integration 2".equals(e.getDescription())));
        
        // Cleanup - delete test data
        allExpenses.stream()
            .filter(e -> e.getDescription().startsWith("MySQL Integration"))
            .forEach(e -> expenseCrudService.deleteExpenseByIdAndUserId(e.getId(), 1L));
    }
}