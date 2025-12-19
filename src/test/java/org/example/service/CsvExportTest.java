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

public class CsvExportTest {
    
    @Mock
    private ExpenseDAO expenseDAO;
    
    @Mock
    private ExpenseIdService idService;

    private CsvExportService csvExportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExpenseCrudService crudService = new ExpenseCrudService(expenseDAO, idService);
        ExpenseFilterService filterService = new ExpenseFilterService(crudService);
        csvExportService = new CsvExportService(crudService, filterService);
    }

    @Test
    void testExportFullList() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15)),
            createExpense(2L, "Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20))
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        String csv = csvExportService.exportAllToCsv();

        assertTrue(csv.contains("ID,Description,Amount,Category,Date"));
        assertTrue(csv.contains("1,Lunch,15.00,Food,2024-01-15"));
        assertTrue(csv.contains("2,Bus,2.50,Transport,2024-01-20"));
    }

    @Test
    void testExportFilteredByCategory() {
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.now()),
            createExpense(2L, "Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.now()),
            createExpense(3L, "Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.now())
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        String csv = csvExportService.exportFilteredToCsv(null, null, "Food");

        assertTrue(csv.contains("Lunch"));
        assertTrue(csv.contains("Dinner"));
        assertFalse(csv.contains("Bus"));
    }

    @Test
    void testExportFilteredByDateRange() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15)),
            createExpense(2L, "Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 2, 5))
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        String csv = csvExportService.exportFilteredToCsv(startDate, endDate, null);

        assertTrue(csv.contains("Lunch"));
        assertFalse(csv.contains("Bus"));
    }

    @Test
    void testExportEmptyList() {
        when(expenseDAO.findAll()).thenReturn(List.of());

        String csv = csvExportService.exportAllToCsv();

        assertEquals("ID,Description,Amount,Category,Date\n", csv);
    }

    @Test
    void testCsvFormatting() {
        List<Expense> expenses = List.of(
            createExpense(1L, "Test", new BigDecimal("10.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 1))
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        String csv = csvExportService.exportAllToCsv();

        String[] lines = csv.split("\n");
        assertEquals(2, lines.length);
        assertEquals("ID,Description,Amount,Category,Date", lines[0]);
        assertEquals("1,Test,10.00,Food,2024-01-01", lines[1]);
    }

    @Test
    void testExportCombinedFilters() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        List<Expense> expenses = Arrays.asList(
            createExpense(1L, "Lunch", new BigDecimal("15.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 1, 15)),
            createExpense(2L, "Bus", new BigDecimal("2.50"), ExpenseCategory.TRANSPORT, LocalDate.of(2024, 1, 20)),
            createExpense(3L, "Dinner", new BigDecimal("25.00"), ExpenseCategory.FOOD, LocalDate.of(2024, 2, 5))
        );
        
        when(expenseDAO.findAll()).thenReturn(expenses);

        String csv = csvExportService.exportFilteredToCsv(startDate, endDate, "Food");

        assertTrue(csv.contains("Lunch"));
        assertFalse(csv.contains("Bus"));
        assertFalse(csv.contains("Dinner"));
    }

    private Expense createExpense(Long id, String description, BigDecimal amount, ExpenseCategory category, LocalDate date) {
        Expense expense = new Expense(description, amount, category, date);
        expense.setId(id);
        return expense;
    }
}