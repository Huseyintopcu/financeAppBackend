package com.example.financeapp.controller;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.entity.Income;
import com.example.financeapp.repository.IncomeRepository;
import com.example.financeapp.service.IncomeService;
import org.apache.catalina.LifecycleState;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;

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

    @GetMapping("/all")
    public List<Income> getAllIncome()
    {
        return incomeService.getALlIncome();
    }

    @DeleteMapping("{id}")
    public void deleteIncome(@PathVariable int id)
    {
        incomeService.deleteIncome(id);
    }
}
