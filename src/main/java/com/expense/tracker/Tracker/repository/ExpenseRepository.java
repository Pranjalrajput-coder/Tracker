package com.expense.tracker.Tracker.repository;

import com.expense.tracker.Tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, String category);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT DISTINCT e.category FROM Expense e WHERE e.user.id = ?1")
    List<String> findCategoriesByUserId(Long userId);
}