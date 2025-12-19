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
    private ExpenseDAO expenseDAO; // all DB filtering are mocked
    
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expenseService = new ExpenseService(expenseDAO);
    }

    @Test
    void testFilterByExistingCategory() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense("Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getFilteredExpenses(null, null, "Food");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getCategoryDisplayName().equals("Food")));
    }

    @Test
    void testFilterByNonExistingCategory() {
        List<Expense> expenses = List.of(
                createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getFilteredExpenses(null, null, "Travel");

        assertEquals(0, result.size());
    }

    @Test
    void testFilterWithEmptyValue() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getFilteredExpenses(null, null, "");

        assertEquals(2, result.size());
    }

    @Test
    void testCaseInsensitiveFiltering() {
        List<Expense> expenses = Arrays.asList(
            createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense("Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getFilteredExpenses(null, null, "food");

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategoryDisplayName());
    }

    @Test
    void testFilterWithWhitespace() {
        List<Expense> expenses = List.of(
                createExpense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getFilteredExpenses(null, null, "  Food  ");

        assertEquals(1, result.size());
    }

    @Test
    public void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> ExpenseCategory.fromString("InvalidCategory"));
    }

    @Test
    public void testValidCategories() {
        for (ExpenseCategory category : ExpenseCategory.values()) {
            Expense expense = createExpense("Test", new BigDecimal("10.00"), category, LocalDate.now());
            assertEquals(category, expense.getCategory());
        }
    }

    @Test
    public void testAmountCalculation() {
        Expense expense1 = createExpense("Test 1", new BigDecimal("10.50"), ExpenseCategory.FOOD, LocalDate.now());
        Expense expense2 = createExpense("Test 2", new BigDecimal("25.75"), ExpenseCategory.TRANSPORT, LocalDate.now());
        Expense expense3 = createExpense("Test 3", new BigDecimal("5.25"), ExpenseCategory.OTHER, LocalDate.now());

        BigDecimal total = expense1.getAmount()
                .add(expense2.getAmount())
                .add(expense3.getAmount());

        assertEquals(new BigDecimal("41.50"), total);
    }

    @Test
    void testGetExpensesByDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Expense expense1 = createExpense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        Expense expense2 = createExpense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20));

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

    @Test
    void testGetFilteredExpensesCombined() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        Expense expense1 = createExpense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        Expense expense2 = createExpense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20));
        Expense expense3 = createExpense("Dinner", new BigDecimal("30.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 2, 5));

        when(expenseDAO.findAll()).thenReturn(Arrays.asList(expense1, expense2, expense3));

        List<Expense> result = expenseService.getFilteredExpenses(startDate, endDate, "Food");

        assertEquals(1, result.size());
        assertEquals("Lunch", result.get(0).getDescription());
    }

    @Test
    void testGetFilteredExpensesNoFilters() {
        Expense expense1 = createExpense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now());
        Expense expense2 = createExpense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.now());

        when(expenseDAO.findAll()).thenReturn(Arrays.asList(expense1, expense2));

        List<Expense> result = expenseService.getFilteredExpenses(null, null, null);

        assertEquals(2, result.size());
    }

    private Expense createExpense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        return new Expense(description, amount, category, date);
    }
}