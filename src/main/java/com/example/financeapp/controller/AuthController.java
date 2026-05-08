package com.example.financeapp.controller;

import com.example.financeapp.dto.LoginRequest;
import com.example.financeapp.dto.LoginResponse;
import com.example.financeapp.dto.RegisterRequest;
import com.example.financeapp.dto.RegisterResponse;
import com.example.financeapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestBody Map<String, String> request) {return authService.sendOtp(request.get("email")).toString();}

    @PostMapping("/verify-otp")
    public boolean verifyOtp(@RequestBody Map<String, String> request) {
        return authService.verifyOtp(
                request.get("email"),
                request.get("code")
        );
    }
}