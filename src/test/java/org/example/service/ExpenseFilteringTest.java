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
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense("Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpenses(null, null, "Food");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getCategoryDisplayName().equals("Food")));
    }

    @Test
    void testFilterByNonExistingCategory() {
        List<Expense> expenses = List.of(
                createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpenses(null, null, "Travel");

        assertEquals(0, result.size());
    }

    @Test
    void testFilterWithEmptyValue() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpenses(null, null, "");

        assertEquals(2, result.size());
    }

    @Test
    void testCaseInsensitiveFiltering() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpenses(null, null, "food");

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategoryDisplayName());
    }

    @Test
    void testFilterWithWhitespace() {
        List<Expense> expenses = List.of(
                createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.getFilteredExpenses(null, null, "  Food  ");

        assertEquals(1, result.size());
    }



    @Test
    void testGetFilteredExpensesCombined() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Expense expense1 = createExpense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        Expense expense2 = createExpense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20));
        Expense expense3 = createExpense("Dinner", new BigDecimal("30.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 2, 5));

        when(expenseDAO.findAll()).thenReturn(Arrays.asList(expense1, expense2, expense3));

        List<Expense> result = filterService.getFilteredExpenses(startDate, endDate, "Food");

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    @Test
    void testGetFilteredExpensesNoFilters() {
        Expense expense1 = createExpense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now());
        Expense expense2 = createExpense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.now());

        when(expenseDAO.findAll()).thenReturn(Arrays.asList(expense1, expense2));

        List<Expense> result = filterService.getFilteredExpenses(null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void testFilterExpensesByCategory() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.filterExpensesByCategory("Food");

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    @Test
    void testFilterExpensesByDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15)),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 2, 5))
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = filterService.filterExpensesByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    private Expense createExpense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        return new Expense(description, amount, category, date);
    }
}