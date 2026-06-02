package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FcmTokenRequest
{
    @NotBlank(message = "E-posta alanı boş olamaz")
    private String email;

    @NotBlank(message = "Token alanı boş olamaz")
    private String token;
}
