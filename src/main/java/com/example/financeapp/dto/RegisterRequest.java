package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest
{
    @NotBlank(message = "Email boş bırakılamz")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz")
    private String password;
}
