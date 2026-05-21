package com.example.financeapp.entity;

import com.example.financeapp.enums.ExpenseCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name= "expenses")
@Data
public class Expense
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private double amount;

    private LocalDate transactionDate;

    private String userEmail;
}
