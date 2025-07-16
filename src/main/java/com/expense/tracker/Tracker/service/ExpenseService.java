package com.expense.tracker.Tracker.service;

import com.expense.tracker.Tracker.entity.Expense;
import com.expense.tracker.Tracker.entity.User;
import com.expense.tracker.Tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(BigDecimal amount, String category, LocalDate date, String note, User user) {
        Expense expense = new Expense(amount, category, date, note, user);
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses(User user) {
        return expenseRepository.findByUserIdOrderByDateDesc(user.getId());
    }

    public List<Expense> getExpensesByCategory(User user, String category) {
        return expenseRepository.findByUserIdAndCategoryOrderByDateDesc(user.getId(), category);
    }

    public List<Expense> getExpensesByDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), startDate, endDate);
    }

    public Expense updateExpense(Long expenseId, BigDecimal amount, String category, LocalDate date, String note, User user) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, user.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setDate(date);
        expense.setNote(note);

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, user.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expenseRepository.delete(expense);
    }

    public Map<String, Object> getMonthlyReport(User user, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<Expense> expenses = getExpensesByDateRange(user, startDate, endDate);

        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryBreakdown = new HashMap<>();
        for (Expense expense : expenses) {
            categoryBreakdown.merge(expense.getCategory(), expense.getAmount(), BigDecimal::add);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalAmount", totalAmount);
        report.put("categoryBreakdown", categoryBreakdown);
        report.put("expenses", expenses);

        return report;
    }

    public List<String> getCategories(User user) {
        return expenseRepository.findCategoriesByUserId(user.getId());
    }
}