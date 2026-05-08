package com.example.financeapp.dto;
import lombok.Data;

@Data
public class LoginResponse
{
    private String token;
    private boolean success;

    public LoginResponse(String token,boolean success)
    {
        this.token=token;
        this.success=success;
    }

}
