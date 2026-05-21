package com.example.financeapp.dto;

import com.example.financeapp.enums.ExpenseCategory;
import lombok.Data;


@Data
public class ExpenseRequest
{
    private String title;
    private double amount;
    private int quantity;
    private ExpenseCategory category;
}
