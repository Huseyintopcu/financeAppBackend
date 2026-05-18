package com.example.financeapp.controller;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.repository.IncomeRepository;
import com.example.financeapp.service.IncomeService;
import org.springframework.web.bind.annotation.*;

import java.io.Console;

@RestController
@RequestMapping("/income")
public class IncomeController
{
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService)
    {
        this.incomeService = incomeService;
    }

    @PostMapping("/add")
    public IncomeResponse addIncome(@RequestBody IncomeRequest request)
    {
        return incomeService.addIncome(request);
    }

    @GetMapping("/monthly-total")
    public double getMontlyTotal()
    {
        return incomeService.getCurrentMonthIncome();
    }
}
