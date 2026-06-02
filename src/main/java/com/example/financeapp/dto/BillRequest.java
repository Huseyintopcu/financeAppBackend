package com.example.financeapp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Data
public class BillRequest
{
    @NotBlank(message = "Fatura başlığı boş olamaz")
    private String title;

    @NotNull(message = "Fatura tutarı boş olamaz")
    @Positive(message = "Fatura tutarı sıfırdan büyük olmalıdır")
    private Double amount;

    @NotNull(message = "Son ödeme tarihi boş olamaz")
    private LocalDate finalPaymentDate;
}
