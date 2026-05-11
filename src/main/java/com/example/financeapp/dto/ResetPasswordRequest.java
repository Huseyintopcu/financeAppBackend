package com.example.financeapp.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest
{
    private String email;
    private String password;
}
