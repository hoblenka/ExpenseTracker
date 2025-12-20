package org.example.service;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.example.dao.ExpenseDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseFilteringTest {
    
    @Mock
    private ExpenseDAO expenseDAO;

    @Mock
    private ExpenseIdService idService;

    private ExpenseFilterService filterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExpenseCrudService crudService = new ExpenseCrudService(expenseDAO, idService);
        filterService = new ExpenseFilterService(crudService);
    }

    @Test
    void testFilterByExistingCategory() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId),
            createExpenseForUser("Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, "Food", userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getCategoryDisplayName().equals("Food")));
    }

    @Test
    void testFilterByNonExistingCategory() {
        Long userId = 1L;
        List<Expense> expenses = List.of(
                createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, "Travel", userId);

        assertEquals(0, result.size());
    }

    @Test
    void testFilterWithEmptyValue() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, "", userId);

        assertEquals(2, result.size());
    }

    @Test
    void testCaseInsensitiveFiltering() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, "food", userId);

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategoryDisplayName());
    }

    @Test
    void testFilterWithWhitespace() {
        Long userId = 1L;
        List<Expense> expenses = List.of(
                createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, "  Food  ", userId);

        assertEquals(1, result.size());
    }



    @Test
    void testGetFilteredExpensesCombined() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Expense expense1 = createExpenseForUser("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15), userId);
        Expense expense2 = createExpenseForUser("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20), userId);
        Expense expense3 = createExpenseForUser("Dinner", new BigDecimal("30.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 2, 5), userId);

        when(expenseDAO.findAllByUserId(userId)).thenReturn(Arrays.asList(expense1, expense2, expense3));

        List<Expense> result = filterService.getFilteredExpensesByUserId(startDate, endDate, "Food", userId);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    @Test
    void testGetFilteredExpensesNoFilters() {
        Long userId = 1L;
        Expense expense1 = createExpenseForUser("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        Expense expense2 = createExpenseForUser("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId);

        when(expenseDAO.findAllByUserId(userId)).thenReturn(Arrays.asList(expense1, expense2));

        List<Expense> result = filterService.getFilteredExpensesByUserId(null, null, null, userId);

        assertEquals(2, result.size());
    }

    @Test
    void testFilterExpensesByCategory() {
        Long userId = 1L;
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now(), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now(), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.filterExpensesByCategoryAndUserId("Food", userId);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    @Test
    void testFilterExpensesByDateRange() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        List<Expense> expenses = Arrays.asList(
            createExpenseForUser("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15), userId),
            createExpenseForUser("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 2, 5), userId)
        );
        
        when(expenseDAO.findAllByUserId(userId)).thenReturn(expenses);

        List<Expense> result = filterService.filterExpensesByDateRangeAndUserId(startDate, endDate, userId);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    private Expense createExpenseForUser(String description, BigDecimal amount, ExpenseCategory category, LocalDate date, Long userId) {
        return new Expense(description, amount, category, date, userId);
    }
}