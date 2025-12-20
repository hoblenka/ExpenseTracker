package org.example.dao;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseIdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseDAOMultiUserTest {

    @Mock
    private DataSource dataSource;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private ResultSet resultSet;
    
    @Mock
    private ExpenseIdService expenseIdService;

    private ExpenseDAOImpl expenseDAO;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        expenseDAO = new ExpenseDAOImpl();
        expenseDAO.dataSource = dataSource;
        expenseDAO.expenseIdService = expenseIdService;
        
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void testFindAllByUserId_ReturnsOnlyUserExpenses() throws SQLException {
        Long userId = 1L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("50.00"));
        when(resultSet.getString("category")).thenReturn("Food");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("User 1 expense");
        when(resultSet.getLong("user_id")).thenReturn(userId);

        List<Expense> result = expenseDAO.findAllByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(preparedStatement).setLong(1, userId);
        verify(connection).prepareStatement("SELECT * FROM expenses WHERE user_id = ?");
    }

    @Test
    void testFindByIdAndUserId_ReturnsExpenseForCorrectUser() throws SQLException {
        Long expenseId = 1L;
        Long userId = 1L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("id")).thenReturn(expenseId);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("25.00"));
        when(resultSet.getString("category")).thenReturn("Transport");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("Bus ticket");
        when(resultSet.getLong("user_id")).thenReturn(userId);

        Expense result = expenseDAO.findByIdAndUserId(expenseId, userId);

        assertNotNull(result);
        assertEquals(expenseId, result.getId());
        assertEquals(userId, result.getUserId());
        verify(preparedStatement).setLong(1, expenseId);
        verify(preparedStatement).setLong(2, userId);
    }

    @Test
    void testFindByIdAndUserId_ReturnsNullForWrongUser() throws SQLException {
        long expenseId = 1L;
        long userId = 2L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Expense result = expenseDAO.findByIdAndUserId(expenseId, userId);

        assertNull(result);
        verify(preparedStatement).setLong(1, expenseId);
        verify(preparedStatement).setLong(2, userId);
    }

    @Test
    void testSaveExpense_IncludesUserId() throws SQLException {
        long userId = 1L;
        long nextId = 5L;
        Expense expense = new Expense("Test expense", new BigDecimal("30.00"), ExpenseCategory.FOOD, LocalDate.now(), userId);
        
        when(expenseIdService.getNextAvailableId()).thenReturn(nextId);

        expenseDAO.save(expense);

        verify(preparedStatement).setLong(1, nextId);
        verify(preparedStatement).setBigDecimal(2, expense.getAmount());
        verify(preparedStatement).setString(3, expense.getCategoryDisplayName());
        verify(preparedStatement).setDate(4, Date.valueOf(expense.getDate()));
        verify(preparedStatement).setString(5, expense.getDescription());
        verify(preparedStatement).setLong(6, userId);
        verify(connection).prepareStatement("INSERT INTO expenses (id, amount, category, date, description, user_id) VALUES (?, ?, ?, ?, ?, ?)");
    }

    @Test
    void testDeleteByIdAndUserId_OnlyDeletesUserExpense() throws SQLException {
        long expenseId = 1L;
        long userId = 1L;

        expenseDAO.deleteByIdAndUserId(expenseId, userId);

        verify(preparedStatement).setLong(1, expenseId);
        verify(preparedStatement).setLong(2, userId);
        verify(connection).prepareStatement("DELETE FROM expenses WHERE id = ? AND user_id = ?");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testFindByCategoryAndUserId_FiltersCorrectly() throws SQLException {
        String category = "Food";
        Long userId = 1L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("15.00"));
        when(resultSet.getString("category")).thenReturn(category);
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("Lunch");
        when(resultSet.getLong("user_id")).thenReturn(userId);

        List<Expense> result = expenseDAO.findByCategoryAndUserId(category, userId);

        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategoryDisplayName());
        assertEquals(userId, result.get(0).getUserId());
        verify(preparedStatement).setString(1, category);
        verify(preparedStatement).setLong(2, userId);
    }

    @Test
    void testFindByDateRangeAndUserId_FiltersCorrectly() throws SQLException {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Long userId = 1L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("40.00"));
        when(resultSet.getString("category")).thenReturn("Shopping");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.of(2024, 1, 15)));
        when(resultSet.getString("description")).thenReturn("Groceries");
        when(resultSet.getLong("user_id")).thenReturn(userId);

        List<Expense> result = expenseDAO.findByDateRangeAndUserId(startDate, endDate, userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(preparedStatement).setDate(1, Date.valueOf(startDate));
        verify(preparedStatement).setDate(2, Date.valueOf(endDate));
        verify(preparedStatement).setLong(3, userId);
    }

    @Test
    void testDeleteAllByUserId_OnlyDeletesUserExpenses() throws SQLException {
        long userId = 1L;

        expenseDAO.deleteAllByUserId(userId);

        verify(preparedStatement).setLong(1, userId);
        verify(connection).prepareStatement("DELETE FROM expenses WHERE user_id = ?");
        verify(preparedStatement).executeUpdate();
    }

    @Test
    void testFindPageByUserId_PaginatesUserExpenses() throws SQLException {
        int page = 0;
        int size = 10;
        Long userId = 1L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getBigDecimal("amount")).thenReturn(new BigDecimal("20.00"));
        when(resultSet.getString("category")).thenReturn("Entertainment");
        when(resultSet.getDate("date")).thenReturn(Date.valueOf(LocalDate.now()));
        when(resultSet.getString("description")).thenReturn("Movie");
        when(resultSet.getLong("user_id")).thenReturn(userId);

        List<Expense> result = expenseDAO.findPageByUserId(page, size, userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(preparedStatement).setLong(1, userId);
        verify(preparedStatement).setInt(2, size);
        verify(preparedStatement).setInt(3, 0);
    }

    @Test
    void testCountAllByUserId_CountsOnlyUserExpenses() throws SQLException {
        long userId = 1L;
        long expectedCount = 5L;
        
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong(1)).thenReturn(expectedCount);

        long result = expenseDAO.countAllByUserId(userId);

        assertEquals(expectedCount, result);
        verify(preparedStatement).setLong(1, userId);
        verify(connection).prepareStatement("SELECT COUNT(*) FROM expenses WHERE user_id = ?");
    }
}