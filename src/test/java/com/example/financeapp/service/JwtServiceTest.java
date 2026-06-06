package com.example.financeapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testEmail = "user@finance.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // En az 256 bit uzunluğunda güvenli bir test secret key tanımlıyoruz
        String testSecret = "mysecretkeymysecretkeymysecretkeymysecretkey123456";

        // @Value ile okunan private 'secret' alanına test anahtarını enjekte ediyoruz
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);

        // @PostConstruct metodunu test ortamında elle tetikleyerek 'key' nesnesini oluşturuyoruz
        jwtService.init();
    }

    @Test
    void generateAccessToken_gecerli_bir_token_uretmeli_ve_icerisinden_email_okunabilmeli() {
        // When: Access Token üret
        String token = jwtService.generateAccessToken(testEmail);

        // Then: Token boş olmamalı ve içindeki e-posta bizim verdiğimiz adres olmalı
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtService.extractEmail(token)).isEqualTo(testEmail);
        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void generateRefreshToken_gecerli_bir_refresh_token_uretmeli() {
        // When: Refresh Token üret
        String refreshToken = jwtService.generateRefreshToken(testEmail);

        // Then: Refresh token geçerli olmalı ve içinden e-posta başarıyla çekilebilmeli
        assertThat(refreshToken).isNotNull().isNotEmpty();
        assertThat(jwtService.validateRefreshToken(refreshToken)).isTrue();
        assertThat(jwtService.extractEmail(refreshToken)).isEqualTo(testEmail);
    }

    @Test
    void isValid_gecersiz_veya_hatali_token_gonderildiginde_false_donmeli() {
        // Given: Rastgele bozuk bir token metni
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.token";

        // When: Tokenı doğrula
        boolean result = jwtService.isValid(invalidToken);

        // Then: Geçersiz olduğu için false dönmeli, uygulama çökmemeli (try-catch testi)
        assertThat(result).isFalse();
    }
}
