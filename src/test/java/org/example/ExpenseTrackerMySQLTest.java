package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.controller.ExpenseController;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseService;
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
    private ExpenseService expenseService;

    @Autowired
    private ExpenseController expenseController;

    @Test
    void testDatabaseConnectionActive() {
        // Simply verify we can read from database without adding data
        assertDoesNotThrow(() -> {
            List<Expense> expenses = expenseService.getAllExpenses();
            assertNotNull(expenses);
        });
    }
    
    @Test
    void testRealDatabaseConnection() {
        // Save expense to real MySQL database
        Expense expense = new Expense("MySQL Database Test", new BigDecimal("10.00"), ExpenseCategory.OTHER, LocalDate.now());
        expenseService.saveExpense(expense);
        
        // Retrieve from database through controller
        List<Expense> result = expenseController.getAllExpenses().getBody();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> "MySQL Database Test".equals(e.getDescription())));
        
        // Cleanup - delete test data
        result.stream()
            .filter(e -> "MySQL Database Test".equals(e.getDescription()))
            .forEach(e -> expenseService.deleteExpense(e.getId()));
    }

    @Test
    void testDatabasePersistence() {
        // Count existing test expenses before adding new ones
        long initialTestCount = expenseService.getAllExpenses().stream()
                .filter(e -> e.getDescription().startsWith("MySQL Integration"))
                .count();
        
        Expense expense1 = new Expense("MySQL Integration 1", new BigDecimal("25.50"), ExpenseCategory.OTHER, LocalDate.now());
        Expense expense2 = new Expense("MySQL Integration 2", new BigDecimal("15.75"), ExpenseCategory.OTHER, LocalDate.now());
        
        expenseService.saveExpense(expense1);
        expenseService.saveExpense(expense2);
        
        // Verify our specific test expenses were added
        List<Expense> allExpenses = expenseService.getAllExpenses();
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
            .forEach(e -> expenseService.deleteExpense(e.getId()));
    }
}