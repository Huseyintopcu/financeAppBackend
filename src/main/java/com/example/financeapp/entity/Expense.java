package com.example.financeapp.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jfr.Enabled;
import lombok.Data;

import java.time.LocalDate;

@Enabled
@Table(name= "expenses")
@Data
public class Expense
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private double amount;

    private LocalDate transactionDate;

    private String userEmail;
}
