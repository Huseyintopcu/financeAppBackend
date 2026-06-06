package com.example.financeapp.controller;

import com.example.financeapp.dto.*;
import com.example.financeapp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    // --- REGISTER TESTS ---
    @Test
    void register_basarili_oldugunda_200_ok_donmeli() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@finance.com");
        request.setPassword("secure123");
        RegisterResponse mockResponse = new RegisterResponse(true, "Kayıt Başarılı");

        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void register_basarisiz_oldugunda_401_unauthorized_donmeli() throws Exception {
        RegisterRequest request = new RegisterRequest("existing@finance.com", "secure123");
        RegisterResponse mockResponse = new RegisterResponse(false, "Email Zaten Kullanılıyor");

        Mockito.when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    // --- LOGIN TESTS ---
    @Test
    void login_basarili_oldugunda_200_ok_ve_tokenlar_donmeli() throws Exception {
        LoginRequest request = new LoginRequest("user@finance.com", "password123");
        LoginResponse mockResponse = new LoginResponse("access_token_xyz", "refresh_token_abc", true);

        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.accessToken").value("access_token_xyz"));
    }

    @Test
    void login_basarisiz_oldugunda_401_unauthorized_donmeli() throws Exception {
        LoginRequest request = new LoginRequest("wrong@finance.com", "badpass");
        LoginResponse mockResponse = new LoginResponse(null, null, false);

        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    // --- SEND OTP TESTS ---
    @Test
    void sendOtp_istek_atildiginda_string_mesaj_donmeli() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "user@finance.com");

        Mockito.when(authService.sendOtp("user@finance.com")).thenReturn("Kod Gönderildi");

        mockMvc.perform(post("/auth/send-otp").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Kod Gönderildi"));
    }

    // --- VERIFY OTP TESTS ---
    @Test
    void verifyOtp_dogru_kod_girildiginde_200_ok_donmeli() throws Exception {
        VerifyOtpRequest request = new VerifyOtpRequest("user@finance.com", "123456");
        VerifyOtpResponse mockResponse = new VerifyOtpResponse(true, "Kod Doğrulandı");

        Mockito.when(authService.verifyOtp(any(VerifyOtpRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/verify-otp").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- RESET PASSWORD TESTS ---
    @Test
    void resetPassword_basarili_oldugunda_200_ok_donmeli() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest("user@finance.com", "newSecure123");
        ResetPasswordResponse mockResponse = new ResetPasswordResponse(true, "Şifre Güncellendi");

        Mockito.when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/reset-password").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- REFRESH TOKEN TESTS ---
    @Test
    void refresh_token_gecerliyse_yeni_tokenlar_donmeli() throws Exception {
        RefreshRequest request = new RefreshRequest("old_refresh_token");
        LoginResponse mockResponse = new LoginResponse("new_access", "new_refresh", true);

        Mockito.when(authService.refresh(any(RefreshRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/auth/refresh").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // --- UPDATE FCM TOKEN TESTS ---
    @Test
    void updateFcmToken_kullanici_varsa_basari_mesaji_donmeli() throws Exception {
        FcmTokenRequest request = new FcmTokenRequest("user@finance.com", "new_token_123");

        Mockito.when(authService.updateFcmToken(any(FcmTokenRequest.class))).thenReturn(true);

        mockMvc.perform(post("/auth/update-fcm-token").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Cihaz token'ı başarıyla güncellendi."));
    }

    @Test
    void updateFcmToken_kullanici_yoksa_400_bad_request_donmeli() throws Exception {
        FcmTokenRequest request = new FcmTokenRequest("wrong@finance.com", "token_123");

        Mockito.when(authService.updateFcmToken(any(FcmTokenRequest.class))).thenReturn(false);

        mockMvc.perform(post("/auth/update-fcm-token").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Kullanıcı bulunamadı."));
    }

    // --- DELETE ACCOUNT TESTS ---
    @Test
    void deleteAccount_tetiklendiginde_200_ok_ve_silindi_mesajı_donmeli() throws Exception {
        String email = "user@finance.com";

        // void dönen metotlar için Mockito'da doNothing kullanılır (varsayılan davranıştır)
        Mockito.doNothing().when(authService).deleteAccount(email);

        mockMvc.perform(delete("/auth/delete").with(csrf())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(content().string("hesap silindi"));
    }
}
