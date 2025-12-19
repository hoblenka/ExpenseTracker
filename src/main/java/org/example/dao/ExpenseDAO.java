package org.example.dao;

import org.example.model.Expense;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseDAO {
    List<Expense> findAll();
    Expense findById(Long id);
    void save(Expense expense);
    void update(Expense expense);
    void deleteById(Long id);
    List<Expense> findByCategory(String category);
    List<Expense> findByDateRange(LocalDate startDate, LocalDate endDate);
    void deleteAll();
    List<Expense> findPage(int page, int size);
    long countAll();
}