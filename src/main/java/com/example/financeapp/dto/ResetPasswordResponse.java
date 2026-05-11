package com.example.financeapp.dto;

import lombok.Data;

@Data
public class ResetPasswordResponse
{
    private boolean success;
    private String message;

    public ResetPasswordResponse(boolean success, String message)
    {
        this.success = success;
        this.message = message;
    }
}
