package org.example.service;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseSortingTest {

    private ExpenseSortService sortService;

    @BeforeEach
    void setUp() {
        sortService = new ExpenseSortService();
    }

    @Test
    void testSortExpensesByAmount() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "A", "50.00", ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(2L, "B", "10.00", ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense(3L, "C", "30.00", ExpenseCategory.UTILITIES, LocalDate.now())
        );

        List<Expense> result = sortService.sortExpenses(expenses, "amount");

        assertEquals(new BigDecimal("10.00"), result.get(0).getAmount());
        assertEquals(new BigDecimal("30.00"), result.get(1).getAmount());
        assertEquals(new BigDecimal("50.00"), result.get(2).getAmount());
    }

    @Test
    void testSortExpensesByDate() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "A", "10.00", ExpenseCategory.FOOD, LocalDate.of(2024, 3, 1)),
            createExpense(2L, "B", "20.00", ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 1)),
            createExpense(3L, "C", "30.00", ExpenseCategory.UTILITIES, LocalDate.of(2024, 2, 1))
        );

        List<Expense> result = sortService.sortExpenses(expenses, "date");

        assertEquals(LocalDate.of(2024, 1, 1), result.get(0).getDate());
        assertEquals(LocalDate.of(2024, 2, 1), result.get(1).getDate());
        assertEquals(LocalDate.of(2024, 3, 1), result.get(2).getDate());
    }

    @Test
    void testSortExpensesByDescription() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "Zebra", "10.00", ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(2L, "apple", "20.00", ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense(3L, "Banana", "30.00", ExpenseCategory.UTILITIES, LocalDate.now())
        );

        List<Expense> result = sortService.sortExpenses(expenses, "description");

        assertEquals("apple", result.get(0).getDescription());
        assertEquals("Banana", result.get(1).getDescription());
        assertEquals("Zebra", result.get(2).getDescription());
    }

    @Test
    void testSortExpensesByCategory() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "A", "10.00", ExpenseCategory.UTILITIES, LocalDate.now()),
            createExpense(2L, "B", "20.00", ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(3L, "C", "30.00", ExpenseCategory.TRANSPORT, LocalDate.now())
        );

        List<Expense> result = sortService.sortExpenses(expenses, "category");

        assertEquals(ExpenseCategory.FOOD, result.get(0).getCategory());
        assertEquals(ExpenseCategory.TRANSPORT, result.get(1).getCategory());
        assertEquals(ExpenseCategory.UTILITIES, result.get(2).getCategory());
    }

    @Test
    void testSortExpensesById() {
        List<Expense> expenses = Arrays.asList(
            createExpense(3L, "A", "10.00", ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(1L, "B", "20.00", ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense(2L, "C", "30.00", ExpenseCategory.UTILITIES, LocalDate.now())
        );

        List<Expense> result = sortService.sortExpenses(expenses, "id");

        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    void testSortExpensesInvalidParameter() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "A", "10.00", ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(2L, "B", "20.00", ExpenseCategory.TRANSPORT, LocalDate.now())
        );

        List<Expense> result = sortService.sortExpenses(expenses, "invalid");

        assertEquals(expenses, result);
    }

    @Test
    void testSortExpensesEmptyList() {
        List<Expense> expenses = List.of();

        List<Expense> result = sortService.sortExpenses(expenses, "amount");

        assertTrue(result.isEmpty());
    }

    private Expense createExpense(Long id, String description, String amount, ExpenseCategory category, LocalDate date) {
        Expense expense = new Expense(description, new BigDecimal(amount), category, date);
        expense.setId(id);
        return expense;
    }
}