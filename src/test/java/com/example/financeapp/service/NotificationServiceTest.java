package com.example.financeapp.service;

import com.example.financeapp.entity.Bill;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.BillRepository;
import com.example.financeapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FcmService fcmService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void checkUpcomingBills_kritik_fatura_oldugunda_kullaniciyi_bulup_bildirim_atmalı() {
        // Given (Hazırlık)
        String testEmail = "user@finance.com";
        String mockToken = "fcm_token_xyz";

        // Sahte Fatura oluşturuyoruz (Son ödeme tarihine 2 gün var)
        Bill mockBill = new Bill();
        mockBill.setTitle("İnternet Faturası");
        mockBill.setAmount(199.90);
        mockBill.setUserEmail(testEmail);
        mockBill.setFinalPaymentDate(LocalDate.now().plusDays(2));

        // Sahte Kullanıcı oluşturuyoruz
        User mockUser = new User();
        mockUser.setEmail(testEmail);
        mockUser.setFcmToken(mockToken);

        // Mockito Emirleri: Kritik faturaları dön, kullanıcıyı bul
        when(billRepository.findByIsPaidFalseAndFinalPaymentDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(mockBill));
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(mockUser));

        // When (Aksiyon - Scheduled metodu elle tetikliyoruz)
        notificationService.checkUpcomingBills();

        // Then (Doğrulama)
        // fcmService.sendPushNotification metodunun doğru parametrelerle 1 kez tetiklendiğini kesinleştir
        verify(fcmService, times(1)).sendPushNotification(
                eq(mockToken),
                eq("🚨 Fatura Hatırlatıcı"),
                contains("İnternet Faturası")
        );
    }
}
