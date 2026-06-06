package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest
{
    @NotBlank(message = "Kod doğrulamada email boş bırakılamaz")
    private String email;

    @NotBlank(message = "Kod doğrulamada code boş bırakılamaz")
    private String code;
}
