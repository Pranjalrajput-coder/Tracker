package com.expense.tracker.Tracker.controller;

import com.expense.tracker.Tracker.dto.ExpenseRequest;
import com.expense.tracker.Tracker.entity.Expense;
import com.expense.tracker.Tracker.entity.User;
import com.expense.tracker.Tracker.service.ExpenseService;
import com.expense.tracker.Tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody ExpenseRequest request, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        Expense expense = expenseService.createExpense(
                request.getAmount(),
                request.getCategory(),
                request.getDate(),
                request.getNote(),
                user
        );
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Authentication auth) {

        User user = userService.findByUsername(auth.getName());
        List<Expense> expenses;

        if (category != null) {
            expenses = expenseService.getExpensesByCategory(user, category);
        } else if (startDate != null && endDate != null) {
            expenses = expenseService.getExpensesByDateRange(user, startDate, endDate);
        } else {
            expenses = expenseService.getAllExpenses(user);
        }

        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        Expense expense = expenseService.updateExpense(
                id,
                request.getAmount(),
                request.getCategory(),
                request.getDate(),
                request.getNote(),
                user
        );
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        expenseService.deleteExpense(id, user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Expense deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month,
            Authentication auth) {

        User user = userService.findByUsername(auth.getName());
        Map<String, Object> report = expenseService.getMonthlyReport(user, year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(Authentication auth) {
        User user = userService.findByUsername(auth.getName());
        List<String> categories = expenseService.getCategories(user);
        return ResponseEntity.ok(categories);
    }
}