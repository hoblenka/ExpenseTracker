package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseCrudService;
import org.example.service.ExpenseFilterService;
import org.example.service.ExpensePaginationService;
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
import java.util.List;

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
    private ExpensePaginationService expensePaginationService;

    @Mock
    private ExpenseController expenseController;

    @Mock
    private Model model;

    private WebExpenseController webController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webController = new WebExpenseController(expenseCrudService, filterService, sortService, expensePaginationService, expenseController);
    }

    @Test
    void testListExpensesWithDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        ExpensePaginationService.PageResult<Expense> pageResult = 
            new ExpensePaginationService.PageResult<>(List.of(expense), 0, 10, 1, 1);
        
        when(filterService.getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null))
                .thenReturn(List.of(expense));
        when(expensePaginationService.getPageFromList(List.of(expense), 0, 10)).thenReturn(pageResult);

        String result = webController.listExpenses(startDate, endDate, null, null, 0, 10, model);

        assertEquals("list", result);
        verify(filterService).getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null);
        verify(model).addAttribute("expenses", List.of(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
        verify(model).addAttribute("startDate", startDate);
        verify(model).addAttribute("endDate", endDate);
    }

    @Test
    void testListExpensesWithoutDateRange() {
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.now());
        ExpensePaginationService.PageResult<Expense> pageResult = 
            new ExpensePaginationService.PageResult<>(List.of(expense), 0, 10, 1, 1);
        
        when(expenseCrudService.getAllExpenses()).thenReturn(List.of(expense));
        when(expensePaginationService.getPageFromList(List.of(expense), 0, 10)).thenReturn(pageResult);

        String result = webController.listExpenses(null, null, null, null, 0, 10, model);

        assertEquals("list", result);
        verify(expenseCrudService).getAllExpenses();
        verify(model).addAttribute("expenses", List.of(expense));
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
        String result = webController.deleteAllExpenses(null, null, null, null, 0, 10);
        assertTrue(result.startsWith("redirect:/expenses?"));
        verify(expenseCrudService).deleteAllExpenses();
    }

    @Test
    void testAddRandomExpense() {
        String result = webController.addRandomExpense(null, null, null, null, 0, 10);
        assertTrue(result.startsWith("redirect:/expenses?"));
        verify(expenseCrudService).addRandomExpense();
    }
}