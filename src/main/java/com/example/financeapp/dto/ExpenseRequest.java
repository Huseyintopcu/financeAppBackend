package com.example.financeapp.dto;

import com.example.financeapp.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class ExpenseRequest
{
    @NotBlank(message = "Gider başlığı boş olamaz")
    private String title;

    @NotNull(message = "Gider tutarı boş olamaz")
    @Positive(message = "Gider tutarı sıfırdan büyük olmalıdır")
    private double amount;

    @NotNull(message = "Gider adeti boş bırakılamaz")
    @Positive(message = "Gider adeti sıfırdan büyük olmalıdır")
    private int quantity;

    @NotNull(message = "Gider kategorisi boş bırakılamaz")
    private ExpenseCategory category;
}
