package com.example.financeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest
{
    @NotBlank(message = "Email boş bırakılamz")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz")
    private String password;
}
