package com.example.financeapp.service;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.repository.ExpenseRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenseService
{
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository)
    {
        this.expenseRepository = expenseRepository;
    }

    // Add new expense
    public ExpenseResponse addExpense(ExpenseRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setQuantity(request.getQuantity());
        expense.setCategory(request.getCategory());
        expense.setTransactionDate(LocalDate.now());
        expense.setUserEmail(email);

        expenseRepository.save(expense);

        return new ExpenseResponse(true,"Gider Eklendi");
    }

    // Get total expense amount of last month
    public double getMonthlyExpense()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();

        return expenseRepository.getMonthlyExpense(email, now.getMonthValue(), now.getYear());
    }

    // Get a expense list of the last month
    public List<Expense> getAllExpense()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return expenseRepository.findByUserEmailAndTransactionDateBetween(email,start,end);
    }

    // Delete chosen expense
    public void deleteExpense(long id)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Expense expense = expenseRepository.findById(id).orElseThrow();

        if (!expense.getUserEmail().equals(email))
        {
            throw new RuntimeException("Yetkisiz işlem");
        }

        expenseRepository.delete(expense);
    }

}
