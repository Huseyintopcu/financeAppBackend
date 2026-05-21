package com.example.financeapp.controller;

import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.repository.ExpenseRepository;
import com.example.financeapp.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense")
public class ExpenseController
{
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService)
    {
        this.expenseService = expenseService;
    }

    @PostMapping("/add")
    public ExpenseResponse addExpense (@RequestBody ExpenseRequest request)
    {
        return expenseService.addExpense(request);
    }

    @GetMapping("/monthly-total")
    public double getMontlyExpense()
    {
        return expenseService.getMonthlyExpense();
    }
}
