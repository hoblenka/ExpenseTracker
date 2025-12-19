package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpenseSortService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebExpenseControllerTest {

    @Mock
    private ExpenseCrudService expenseCrudService;
    
    @Mock
    private ExpenseFilterService filterService;
    
    @Mock
    private ExpenseSortService sortService;

    @Mock
    private Model model;

    private WebExpenseController webController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExpenseController expenseController = new ExpenseController(expenseCrudService, filterService, sortService);
        webController = new WebExpenseController(expenseCrudService, filterService, sortService, expenseController);
    }

    @Test
    void testListExpensesWithDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        
        when(filterService.getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null))
                .thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(startDate, endDate, null, null, model);

        assertEquals("list", result);
        verify(filterService).getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
        verify(model).addAttribute("startDate", startDate);
        verify(model).addAttribute("endDate", endDate);
    }

    @Test
    void testListExpensesWithoutDateRange() {
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(filterService.getFilteredExpenses(null, null, null)).thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(null, null, null, null, model);

        assertEquals("list", result);
        verify(filterService).getFilteredExpenses(null, null, null);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
    }

    @Test
    void testShowAddForm() {
        String result = webController.showAddForm();
        assertEquals("add", result);
    }

    @Test
    void testDeleteExpense() {
        String result = webController.deleteExpense(1L);
        assertEquals("redirect:/expenses", result);
        verify(expenseCrudService).deleteExpense(1L);
    }

    @Test
    void testHome() {
        String result = webController.home();
        assertEquals("redirect:/expenses", result);
    }

    @Test
    void testDeleteAllExpenses() {
        String result = webController.deleteAllExpenses();
        assertEquals("redirect:/expenses", result);
        verify(expenseCrudService).deleteAllExpenses();
    }

    @Test
    void testAddRandomExpense() {
        String result = webController.addRandomExpense();
        assertEquals("redirect:/expenses", result);
        verify(expenseCrudService).addRandomExpense();
    }
}