package org.example.dao;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExpenseDAOImplTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private ResultSet resultSet;

    private ExpenseDAOImpl expenseDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        expenseDAO = new ExpenseDAOImpl();
        expenseDAO.dataSource = dataSource;
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
    }

    @Test
    void testFindByDateRange() throws SQLException {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("50.00"));
        when(resultSet.getString("category")).thenReturn("Food");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.of(2024, 1, 15)));
        when(resultSet.getString("description")).thenReturn("Test expense");

        List<Expense> result = expenseDAO.findByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        verify(preparedStatement).setDate(1, Date.valueOf(startDate));
        verify(preparedStatement).setDate(2, Date.valueOf(endDate));
    }

    @Test
    void testFindByDateRangeEmpty() throws SQLException {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        
        when(resultSet.next()).thenReturn(false);

        List<Expense> result = expenseDAO.findByDateRange(startDate, endDate);

        assertEquals(0, result.size());
    }

    @Test
    void testFindByCategoryExisting() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("25.00"));
        when(resultSet.getString("category")).thenReturn("Food");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("Lunch");

        List<Expense> result = expenseDAO.findByCategory("Food");

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).getCategoryDisplayName());
        verify(preparedStatement).setString(1, "Food");
    }

    @Test
    void testFindByCategoryNonExisting() throws SQLException {
        when(resultSet.next()).thenReturn(false);

        List<Expense> result = expenseDAO.findByCategory("Travel");

        assertEquals(0, result.size());
        verify(preparedStatement).setString(1, "Travel");
    }

    @Test
    void testFindByCategoryCaseInsensitive() throws SQLException {
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("15.00"));
        when(resultSet.getString("category")).thenReturn("Food");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("Coffee");

        List<Expense> result = expenseDAO.findByCategory("food");

        assertEquals(1, result.size());
        verify(preparedStatement).setString(1, "food");
        verify(connection).prepareStatement("SELECT * FROM expenses WHERE LOWER(category) = LOWER(?)");
    }
}