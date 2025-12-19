package org.example.dao;

import org.example.model.Expense;
import java.util.List;

public interface ExpenseDAO {
    List<Expense> findAll();
    Expense findById(Long id);
    void save(Expense expense);
    void update(Expense expense);
    void deleteById(Long id);
    List<Expense> findByCategory(String category);
    void deleteAll();
}