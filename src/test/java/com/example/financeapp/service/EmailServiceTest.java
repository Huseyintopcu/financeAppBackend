package com.example.financeapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendOtp_eposta_ve_kod_gonderildiginde_mesaj_nesnesini_dogru_olusturmali() {
        // Given (Hazırlık)
        String targetEmail = "test@finance.com";
        String secretCode = "987654";

        // ArgumentCaptor, metodun içine gönderilen gerçek nesneyi yakalamamızı sağlar
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // When (Aksiyon)
        emailService.sendOtp(targetEmail, secretCode);

        // Then (Doğrulama)
        // mailSender.send() metodunun tam 1 kez çalıştırıldığını doğrula ve içine giden mesajı yakala
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        // Yakalanan e-posta mesajının detaylarını doğrula
        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getTo()).containsExactly(targetEmail);
        assertThat(capturedMessage.getSubject()).isEqualTo("OTP Kodu");
        assertThat(capturedMessage.getText()).contains(secretCode);
    }
}
