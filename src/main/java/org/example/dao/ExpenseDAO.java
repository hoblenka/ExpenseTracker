package org.example.dao;

import org.example.model.Expense;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseDAO {
    List<Expense> findAll();
    List<Expense> findAllByUserId(Long userId);
    Expense findById(Long id);
    Expense findByIdAndUserId(Long id, Long userId);
    void save(Expense expense);
    void update(Expense expense);
    void deleteById(Long id);
    void deleteByIdAndUserId(Long id, Long userId);
    List<Expense> findByCategory(String category);
    List<Expense> findByCategoryAndUserId(String category, Long userId);
    List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Expense> findByDateRangeAndUserId(LocalDate startDate, LocalDate endDate, Long userId);
    void deleteAll();
    void deleteAllByUserId(Long userId);
    List<Expense> findPage(int page, int size);
    List<Expense> findPageByUserId(int page, int size, Long userId);
    long countAll();
    long countAllByUserId(Long userId);
}