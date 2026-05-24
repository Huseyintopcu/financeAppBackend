package com.example.financeapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionResponse
{
    private String type;
    private String title;
    private double amount;
    private String category;
    private LocalDate transactionDate;

    public TransactionResponse(String type, String title, double amount, String category, LocalDate transactionDate)
    {
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.transactionDate = transactionDate;
    }
}
