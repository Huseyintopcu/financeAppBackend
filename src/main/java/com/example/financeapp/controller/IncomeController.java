package com.example.financeapp.controller;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.repository.IncomeRepository;
import com.example.financeapp.service.IncomeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
