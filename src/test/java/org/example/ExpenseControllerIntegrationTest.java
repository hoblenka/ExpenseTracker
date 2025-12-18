package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.controller.ExpenseController;
import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integration.properties")
@Transactional
class ExpenseControllerIntegrationTest {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseController expenseController;

    @Test
    @Rollback
    void testRealDatabaseConnection() {
        // Save expense to real MySQL database
        Expense expense = new Expense("MySQL Database Test", new BigDecimal("10.00"), "Testing", LocalDate.now());
        expenseRepository.save(expense);
        
        // Retrieve from database through controller
        List<Expense> result = expenseController.getAllExpenses().getBody();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(e -> "MySQL Database Test".equals(e.getDescription())));
    }

    @Test
    @Rollback
    void testDatabasePersistence() {
        long initialCount = expenseRepository.count();
        
        Expense expense1 = new Expense("MySQL Integration 1", new BigDecimal("25.50"), "Database", LocalDate.now());
        Expense expense2 = new Expense("MySQL Integration 2", new BigDecimal("15.75"), "Database", LocalDate.now());
        
        expenseRepository.saveAll(List.of(expense1, expense2));
        
        assertEquals(initialCount + 2, expenseRepository.count());
        
        List<Expense> testExpenses = expenseRepository.findByDescriptionStartingWith("MySQL Integration");
        
        assertEquals(2, testExpenses.size());
    }

    @Test
    void testDatabaseConnectionActive() {
        // Verify database connection is working
        assertTrue(expenseRepository.count() >= 0);
        
        Expense testExpense = new Expense("Connection Test", new BigDecimal("1.00"), "Test", LocalDate.now());
        Expense saved = expenseRepository.save(testExpense);

        assertNotNull(saved.getId());
        assertTrue(expenseRepository.existsById(saved.getId()));
        
        expenseRepository.delete(saved);
    }
}