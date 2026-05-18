package com.example.financeapp.service;

import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.repository.ExpenseRepository;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpenseService
{
    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository)
    {
        this.expenseRepository = expenseRepository;
    }

    public ExpenseResponse addExpense(ExpenseRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Expense expense = new Expense();

        expense.setAmount(request.getAmount());
        expense.setTitle(request.getTitle());
        expense.setTransactionDate(LocalDate.now());
        expense.setUserEmail(email);

        expenseRepository.save(expense);

        return new ExpenseResponse(true,"Gider Eklendi");
    }

    public double getMonthlyExpense()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();

        return expenseRepository.getMonthlyExpense(email, now.getMonthValue(), now.getYear());
    }
}
