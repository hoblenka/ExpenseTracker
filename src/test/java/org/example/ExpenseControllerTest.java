package org.example;

import org.example.controller.ExpenseController;
import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ExpenseControllerTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseController expenseController;

    @Test
    void testGetAllExpenses() {
        Expense expense1 = new Expense("Coffee", new BigDecimal("5.50"), "Food", LocalDate.now());
        Expense expense2 = new Expense("Gas", new BigDecimal("45.00"), "Transportation", LocalDate.now());
        
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense1, expense2));
        
        List<Expense> result = expenseController.getAllExpenses();
        
        assertEquals(2, result.size());
        assertEquals("Coffee", result.get(0).getDescription());
    }
}