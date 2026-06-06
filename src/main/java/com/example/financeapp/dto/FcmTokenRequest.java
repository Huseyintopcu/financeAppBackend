package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequest
{
    @NotBlank(message = "E-posta alanı boş olamaz")
    private String email;

    @NotBlank(message = "Token alanı boş olamaz")
    private String token;
}
