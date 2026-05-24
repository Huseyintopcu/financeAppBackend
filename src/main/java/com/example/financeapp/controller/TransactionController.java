package com.example.financeapp.controller;

import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.service.TransactionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController
{
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    @GetMapping("/today")
    public List<TransactionResponse> getTodayTransactions()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        return transactionService.getTodayTransactions(email);
    }
}
