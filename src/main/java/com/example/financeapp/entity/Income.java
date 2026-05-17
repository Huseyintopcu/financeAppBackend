package com.example.financeapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table (name = "income")
@Data
public class Income
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private double amount;

    private LocalDate transactionDate;

    private String userEmail;
}
