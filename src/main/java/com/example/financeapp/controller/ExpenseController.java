package com.example.financeapp.controller;

import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController
{
    private final ExpenseService expenseService;

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

    @GetMapping("/all")
    public List<Expense> gelAllExpense()
    {
        return expenseService.getAllExpense();
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable long id)
    {
        expenseService.deleteExpense(id);
    }

}
