package org.example.dao;

import org.example.model.Expense;
import org.example.model.ExpenseCategory;
import org.example.service.ExpenseIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpenseDAOImpl implements ExpenseDAO {
    
    @Autowired
    DataSource dataSource;

    @Autowired
    @Lazy
    ExpenseIdService expenseIdService;
//The @Lazy annotation tells Spring to create a proxy for ExpenseIdService instead of initializing it immediately, breaking the circular dependency cycle during application startup.

    @Override
    public List<Expense> findAll() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses", e);
        }
        return expenses;
    }

    @Override
    public Expense findById(Long id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExpense(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expense by id", e);
        }
        return null;
    }

    @Override
    public void save(Expense expense) {
        Long nextId = expenseIdService.getNextAvailableId();
        String sql = "INSERT INTO expenses (id, amount, category, date, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, nextId);
            stmt.setBigDecimal(2, expense.getAmount());
            stmt.setString(3, expense.getCategoryDisplayName());
            stmt.setDate(4, Date.valueOf(expense.getDate()));
            stmt.setString(5, expense.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving expense", e);
        }
    }

    @Override
    public void update(Expense expense) {
        String sql = "UPDATE expenses SET amount = ?, category = ?, date = ?, description = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, expense.getAmount());
            stmt.setString(2, expense.getCategoryDisplayName());
            stmt.setDate(3, Date.valueOf(expense.getDate()));
            stmt.setString(4, expense.getDescription());
            stmt.setLong(5, expense.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating expense", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expense", e);
        }
    }

    @Override
    public List<Expense> findByCategory(String category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE LOWER(category) = LOWER(?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses by category", e);
        }
        return expenses;
    }

    @Override
    public List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE date BETWEEN ? AND ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses by date range", e);
        }
        return expenses;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM expenses";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all expenses", e);
        }
    }

    @Override
    public List<Expense> findPage(int page, int size) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC, id DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, size);
            stmt.setInt(2, page * size);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapResultSetToExpense(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching expenses page", e);
        }
        return expenses;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM expenses";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting expenses", e);
        }
        return 0;
    }

    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getLong("id"));
        expense.setAmount(rs.getBigDecimal("amount"));
        expense.setCategory(ExpenseCategory.fromString(rs.getString("category")));
        expense.setDate(rs.getDate("date").toLocalDate());
        expense.setDescription(rs.getString("description"));
        return expense;
    }
}