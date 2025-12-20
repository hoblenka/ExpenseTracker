package org.example.service;

import org.example.dao.ExpenseDAO;
import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ExpenseDAO expenseDAO;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(expenseDAO);
    }

    @Test
    void testGetSpendingByCategory() {
        List<Expense> expenses = List.of(
            new Expense("Lunch", new BigDecimal("15.50"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Bus", new BigDecimal("5.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        when(expenseDAO.findAll()).thenReturn(expenses);

        Map<String, BigDecimal> result = dashboardService.getSpendingByCategory();

        assertEquals(new BigDecimal("40.50"), result.get("Food"));
        assertEquals(new BigDecimal("5.00"), result.get("Transport"));
        assertEquals(2, result.size());
    }

    @Test
    void testGetSpendingByMonth() {
        List<Expense> expenses = List.of(
            new Expense("Expense1", new BigDecimal("100.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15)),
            new Expense("Expense2", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 20)),
            new Expense("Expense3", new BigDecimal("75.00"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 2, 10))
        );
        when(expenseDAO.findAll()).thenReturn(expenses);

        Map<String, BigDecimal> result = dashboardService.getSpendingByMonth();

        assertEquals(new BigDecimal("150.00"), result.get("2024-01"));
        assertEquals(new BigDecimal("75.00"), result.get("2024-02"));
        assertEquals(2, result.size());
    }

    @Test
    void testGetTotalSpending() {
        List<Expense> expenses = List.of(
            new Expense("Expense1", new BigDecimal("100.00"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Expense2", new BigDecimal("50.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        when(expenseDAO.findAll()).thenReturn(expenses);

        BigDecimal result = dashboardService.getTotalSpending();

        assertEquals(new BigDecimal("150.00"), result);
    }

    @Test
    void testGetTotalExpenseCount() {
        List<Expense> expenses = List.of(
            new Expense("Expense1", new BigDecimal("100.00"), ExpenseCategory.FOOD, LocalDate.now()),
            new Expense("Expense2", new BigDecimal("50.00"), ExpenseCategory.TRANSPORT, LocalDate.now())
        );
        when(expenseDAO.findAll()).thenReturn(expenses);

        long result = dashboardService.getTotalExpenseCount();

        assertEquals(2, result);
    }

    @Test
    void testEmptyDatabase() {
        when(expenseDAO.findAll()).thenReturn(List.of());

        Map<String, BigDecimal> categoryResult = dashboardService.getSpendingByCategory();
        Map<String, BigDecimal> monthlyResult = dashboardService.getSpendingByMonth();
        BigDecimal totalSpending = dashboardService.getTotalSpending();
        long totalCount = dashboardService.getTotalExpenseCount();

        assertTrue(categoryResult.isEmpty());
        assertTrue(monthlyResult.isEmpty());
        assertEquals(BigDecimal.ZERO, totalSpending);
        assertEquals(0, totalCount);
    }
}