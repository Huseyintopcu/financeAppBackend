package com.example.financeapp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseRequest
{
    private String title;
    private double amount;
}
