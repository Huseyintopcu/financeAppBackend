package com.example.financeapp.controller;

import com.example.financeapp.dto.*;
import com.example.financeapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request)
    {
        RegisterResponse response =authService.register(request);
        if (response.isSuccess())
        {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request)
    {
        LoginResponse response = authService.login(request);
        if (response.isSuccess())
        {
            return  ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/send-otp")
    public String sendOtp(@Valid @RequestBody Map<String, String> request)
    {
        return authService.sendOtp(request.get("email")).toString();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request)
    {
        VerifyOtpResponse response = authService.verifyOtp(request);
        if (response.isSuccess())
        {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request)
    {
        ResetPasswordResponse response = authService.resetPassword(request);
        if (response.isSuccess())
        {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request)
    {
        LoginResponse response = authService.refresh(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/update-fcm-token")
    public ResponseEntity<String> updateFcmToken(@Valid @RequestBody FcmTokenRequest request)
    {
        boolean isUpdated = authService.updateFcmToken(request);
        if (isUpdated)
        {
            return ResponseEntity.ok("Cihaz token'ı başarıyla güncellendi.");
        }
        else
        {
            return ResponseEntity.badRequest().body("Kullanıcı bulunamadı.");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody String email)
    {

        System.out.println("CONTROLLER DELETE HIT " + email);

        authService.deleteAccount(email);

        return ResponseEntity.ok("hesap silindi");
    }

    @GetMapping("/ping")
    public ResponseEntity<String> pingServer() {
        return ResponseEntity.ok("Uygulama uyanık ve çalışıyor! 🚀");
    }
}