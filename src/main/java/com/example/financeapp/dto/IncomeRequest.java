package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class IncomeRequest
{
    @NotBlank(message = "Gelir başlığı boş bırakılamaz")
    private String title;

    @NotBlank(message = "Gelir miktarı boş bırakılamaz")
    @Positive(message = "Gelir miktarı sıfırdan büyük olmalıdır")
    private double amount;

    @NotBlank(message = "Gelir tarihi boş bırakılamaz")
    private LocalDate transactionDate;
}
