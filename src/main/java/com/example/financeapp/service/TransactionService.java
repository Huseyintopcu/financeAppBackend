package com.example.financeapp.service;

import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.entity.Income;
import com.example.financeapp.repository.ExpenseRepository;
import com.example.financeapp.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService
{
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public List<TransactionResponse> getTodayTransactions(String email)
    {
        LocalDate today = LocalDate.now();

        List<Income> incomes = incomeRepository.findByUserEmailAndTransactionDate(email,today);
        List<Expense> expenses = expenseRepository.findByUserEmailAndTransactionDate(email,today);

        List<TransactionResponse> result = new ArrayList<>();

        for (Income i : incomes)
        {
            result.add(new TransactionResponse("INCOME", i.getTitle(), i.getAmount(), "INCOME", i.getTransactionDate()));
        }

        for (Expense e : expenses)
        {
            result.add(new TransactionResponse("EXPENSE", e.getTitle(), e.getAmount(), e.getCategory().name(), e.getTransactionDate()));
        }
        return result;
    }
}
