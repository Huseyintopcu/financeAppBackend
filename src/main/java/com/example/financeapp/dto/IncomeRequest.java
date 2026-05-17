package com.example.financeapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeRequest
{
    private String title;
    private double amount;
    private LocalDate transactionDate;
}
