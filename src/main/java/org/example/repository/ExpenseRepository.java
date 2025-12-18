package org.example.repository;

import org.example.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Spring automatically generates the implementation based on the method name convention.
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(String category);
    List<Expense> findByDescriptionStartingWith(String prefix);
}