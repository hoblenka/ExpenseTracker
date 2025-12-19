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

public class ExpenseIdServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;
    
    private ExpenseIdService idService;

    private ExpenseCrudService crudService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idService = new ExpenseIdService(expenseDAO);
        crudService = new ExpenseCrudService(expenseDAO, idService);
    }

    @Test
    void testGetNextAvailableIdEmptyDatabase() {
        when(expenseDAO.findAll()).thenReturn(List.of());

        Long nextId = idService.getNextAvailableId();

        assertEquals(1L, nextId);
    }

    @Test
    void testGetNextAvailableIdSequential() {
        List<Expense> expenses = Arrays.asList(
            createExpenseWithId(1L),
            createExpenseWithId(2L),
            createExpenseWithId(3L)
        );

        when(expenseDAO.findAll()).thenReturn(expenses);

        Long nextId = idService.getNextAvailableId();

        assertEquals(4L, nextId);
    }

    @Test
    void testGetNextAvailableIdWithGap() {
        List<Expense> expenses = Arrays.asList(
            createExpenseWithId(1L),
            createExpenseWithId(3L),
            createExpenseWithId(4L)
        );

        when(expenseDAO.findAll()).thenReturn(expenses);

        Long nextId = idService.getNextAvailableId();

        assertEquals(2L, nextId);
    }

    @Test
    void testGetNextAvailableIdMultipleGaps() {
        List<Expense> expenses = Arrays.asList(
            createExpenseWithId(1L),
            createExpenseWithId(4L),
            createExpenseWithId(6L)
        );

        when(expenseDAO.findAll()).thenReturn(expenses);

        Long nextId = idService.getNextAvailableId();

        assertEquals(2L, nextId);
    }

    @Test
    void testIdReuseAfterDeletion() {
        // Setup: expenses with IDs 1, 3, 4 (ID 2 was deleted)
        List<Expense> expenses = Arrays.asList(
                createExpenseWithId(1L),
                createExpenseWithId(3L),
                createExpenseWithId(4L)
        );

        when(expenseDAO.findAll()).thenReturn(expenses);

        // Create new expense - should get ID 2
        Expense newExpense = createExpense("New", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        crudService.saveExpense(newExpense);

        assertEquals(2L, newExpense.getId());
        verify(expenseDAO).save(newExpense);
    }

    @Test
    void testSequentialIdWhenNoGaps() {
        // Setup: expenses with IDs 1, 2, 3
        List<Expense> expenses = Arrays.asList(
                createExpenseWithId(1L),
                createExpenseWithId(2L),
                createExpenseWithId(3L)
        );

        when(expenseDAO.findAll()).thenReturn(expenses);

        // Create new expense - should get ID 4
        Expense newExpense = createExpense("New", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        crudService.saveExpense(newExpense);

        assertEquals(4L, newExpense.getId());
        verify(expenseDAO).save(newExpense);
    }

    private Expense createExpenseWithId(Long id) {
        Expense expense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        expense.setId(id);
        return expense;
    }

    private Expense createExpense(String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        return new Expense(description, amount, category, date);
    }
}