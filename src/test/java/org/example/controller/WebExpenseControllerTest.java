package org.example.controller;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private Model model;

    private WebExpenseController webController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExpenseController expenseController = new ExpenseController(expenseService);
        webController = new WebExpenseController(expenseService, expenseController);
    }

    @Test
    void testListExpensesWithDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        
        when(expenseService.getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null))
                .thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(startDate, endDate, null, null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
        verify(model).addAttribute("startDate", startDate);
        verify(model).addAttribute("endDate", endDate);
    }

    @Test
    void testListExpensesWithoutDateRange() {
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(expenseService.getFilteredExpenses(null, null, null)).thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(null, null, null, null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(null, null, null);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
    }

    @Test
    void testListExpensesWithCategoryFilter() {
        Expense expense = new Expense("Lunch", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(expenseService.getFilteredExpenses(null, null, "Food")).thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(null, null, "Food", null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(null, null, "Food");
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("25.00"));
        verify(model).addAttribute("category", "Food");
        verify(model).addAttribute(eq("categories"), any());
    }

    @Test
    void testListExpensesWithEmptyCategoryFilter() {
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(expenseService.getFilteredExpenses(null, null, "")).thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(null, null, "", null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(null, null, "");
        verify(model).addAttribute("expenses", Arrays.asList(expense));
    }

    @Test
    void testListExpensesWithNonExistingCategory() {
        when(expenseService.getFilteredExpenses(null, null, "Travel")).thenReturn(Arrays.asList());

        String result = webController.listExpenses(null, null, "Travel", null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(null, null, "Travel");
        verify(model).addAttribute("expenses", Arrays.asList());
        verify(model).addAttribute("totalAmount", BigDecimal.ZERO);
    }

    @Test
    void testListExpensesWithCaseInsensitiveCategory() {
        Expense expense = new Expense("Coffee", new BigDecimal("5.00"), ExpenseCategory.FOOD, LocalDate.now());
        
        when(expenseService.getFilteredExpenses(null, null, "food")).thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(null, null, "food", null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(null, null, "food");
        verify(model).addAttribute("expenses", Arrays.asList(expense));
    }

    @Test
    void testListExpensesWithCombinedFilters() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        String category = "Food";
        Expense expense = new Expense("Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        
        when(expenseService.getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), category))
                .thenReturn(Arrays.asList(expense));

        String result = webController.listExpenses(startDate, endDate, category, null, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), category);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("15.00"));
    }

    @Test
    void testShowAddForm() {
        String result = webController.showAddForm();
        assertEquals("add", result);
    }

    @Test
    void testAddExpenseSuccess() {
        String result = webController.addExpense("Coffee", new BigDecimal("5.50"), "FOOD", "2024-01-15", model);
        assertEquals("redirect:/expenses", result);
        verify(expenseService).saveExpense(any(Expense.class));
    }

    @Test
    void testAddExpenseWithError() {
        String result = webController.addExpense("Coffee", new BigDecimal("5.50"), "INVALID", "2024-01-15", model);

        assertEquals("add", result);
        verify(model).addAttribute("error", "Invalid category: INVALID");
        verify(model).addAttribute("description", "Coffee");
        verify(model).addAttribute("amount", new BigDecimal("5.50"));
        verify(model).addAttribute("category", "INVALID");
        verify(model).addAttribute("date", "2024-01-15");
    }

    @Test
    void testShowEditForm() {
        Expense expense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        when(expenseService.getExpenseById(1L)).thenReturn(expense);

        String result = webController.showEditForm(1L, model);

        assertEquals("edit", result);
        verify(expenseService).getExpenseById(1L);
        verify(model).addAttribute("expense", expense);
    }

    @Test
    void testUpdateExpenseSuccess() {
        String result = webController.updateExpense(1L, "Updated", new BigDecimal("15.00"), "TRANSPORT", "2024-01-15", model);
        assertEquals("redirect:/expenses", result);
        verify(expenseService).updateExpense(any(Expense.class));
    }

    @Test
    void testUpdateExpenseWithError() {
        Expense expense = new Expense("Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.now());
        when(expenseService.getExpenseById(1L)).thenReturn(expense);

        String result = webController.updateExpense(1L, "Updated", new BigDecimal("15.00"), "INVALID", "2024-01-15", model);

        assertEquals("edit", result);
        verify(model).addAttribute("expense", expense);
        verify(model).addAttribute("error", "Invalid category: INVALID");
    }

    @Test
    void testDeleteExpense() {
        String result = webController.deleteExpense(1L);
        assertEquals("redirect:/expenses", result);
        verify(expenseService).deleteExpense(1L);
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
        verify(expenseService).deleteAllExpenses();
    }

    @Test
    void testAddRandomExpense() {
        String result = webController.addRandomExpense();
        assertEquals("redirect:/expenses", result);
        verify(expenseService).addRandomExpense();
    }

    @Test
    void testFilterExpensesByDateRange() {
        String startDate = "2024-01-01";
        String endDate = "2024-01-31";
        Expense expense = new Expense("Test", new BigDecimal("50.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15));
        
        when(expenseService.getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null))
                .thenReturn(Arrays.asList(expense));

        String result = webController.filterExpensesByDateRange(startDate, endDate, model);

        assertEquals("list", result);
        verify(expenseService).getFilteredExpenses(LocalDate.parse(startDate), LocalDate.parse(endDate), null);
        verify(model).addAttribute("expenses", Arrays.asList(expense));
        verify(model).addAttribute("totalAmount", new BigDecimal("50.00"));
        verify(model).addAttribute("startDate", startDate);
        verify(model).addAttribute("endDate", endDate);
    }
}