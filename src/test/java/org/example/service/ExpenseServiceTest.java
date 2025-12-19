package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseService = new ExpenseService(expenseDAO);
    }

    @Test
    void testGetExpensesByDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        Expense expense1 = new Expense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        Expense expense2 = new Expense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20));
        
        when(expenseDAO.findByDateRange(startDate, endDate)).thenReturn(Arrays.asList(expense1, expense2));

        List<Expense> result = expenseService.getExpensesByDateRange(startDate, endDate);

        assertEquals(2, result.size());
        verify(expenseDAO).findByDateRange(startDate, endDate);
    }

    @Test
    void testGetExpensesByDateRangeEmpty() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(expenseDAO.findByDateRange(startDate, endDate)).thenReturn(List.of());

        List<Expense> result = expenseService.getExpensesByDateRange(startDate, endDate);

        assertEquals(0, result.size());
        verify(expenseDAO).findByDateRange(startDate, endDate);
    }
}
