package com.example.financeapp.repository;

import com.example.financeapp.entity.Otp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class OtpRepositoryTest {

    @Autowired
    private OtpRepository otpRepository;

    private final String testEmail = "user@finance.com";
    private final String testCode = "123456";

    @BeforeEach
    void setUp() {
        otpRepository.deleteAll();

        // Test için bir OTP kaydı oluşturup kaydediyoruz
        Otp otp = new Otp();
        otp.setEmail(testEmail);
        otp.setCode(testCode);
        otp.setExpireTime(LocalDateTime.now().plusMinutes(5)); // setExpiryDate yerine setExpireTime olarak GÜNCELLENDİ

        // Farklı bir kullanıcı için de kayıt ekleyelim (unique = true kuralına uyması için farklı e-posta)
        Otp otherOtp = new Otp();
        otherOtp.setEmail("other@finance.com");
        otherOtp.setCode("654321");
        otherOtp.setExpireTime(LocalDateTime.now().plusMinutes(5)); // GÜNCELLENDİ

        otpRepository.saveAll(List.of(otp, otherOtp));
    }


    @Test
    void findByEmail_eposta_ile_otp_kaydini_bulmali() {
        // When: Test e-postası ile sorgulama yap
        Optional<Otp> foundOtp = otpRepository.findByEmail(testEmail);

        // Then: Kayıt bulunmalı ve içindeki kod bizim eklediğimiz testCode (123456) olmalı
        assertThat(foundOtp).isPresent();
        assertThat(foundOtp.get().getCode()).isEqualTo(testCode);
    }

    @Test
    void findByEmail_olmayan_eposta_icin_empty_donmeli() {
        // When: Veritabanında olmayan bir e-posta sorgula
        Optional<Otp> foundOtp = otpRepository.findByEmail("olmayan@user.com");

        // Then: Optional nesnesi boş (empty) dönmeli, uygulama çökmemeli
        assertThat(foundOtp).isEmpty();
    }

    @Test
    void deleteByEmail_eposta_ile_ilgili_tüm_otp_kayitlarini_silmeli() {
        // When: Test kullanıcısının OTP kayıtlarını sil
        otpRepository.deleteByEmail(testEmail);

        // Then: Hedef kullanıcının kaydı silinmiş olmalı
        Optional<Otp> deletedOtp = otpRepository.findByEmail(testEmail);
        assertThat(deletedOtp).isEmpty();

        // Geriye sadece diğer kullanıcının 1 adet OTP kaydı kalmış olmalı
        List<Otp> remainingOtps = otpRepository.findAll();
        assertThat(remainingOtps).hasSize(1);
        assertThat(remainingOtps.get(0).getEmail()).isEqualTo("other@finance.com");
    }
}
