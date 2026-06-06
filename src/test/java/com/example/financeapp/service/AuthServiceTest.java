package com.example.financeapp.service;

import com.example.financeapp.dto.*;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService; // Login testi için gerekli JWT bağımlılığı

    @InjectMocks
    private AuthService authService;

    private final String testEmail = "user@finance.com";
    private final String testPassword = "plainPassword123";
    private final String encodedPassword = "encodedPassword123";

    @Test
    void register_yeni_email_ile_basarili_kayit_yapmali() {
        // Given (Hazırlık)
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        // Mockito Emirleri: Veritabanında kullanıcı yok, şifreyi de sahte şekilde kodla
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

        // When (Aksiyon)
        RegisterResponse response = authService.register(request);

        // Then (Doğrulama)
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Kayıt Başarılı");

        // Veritabanına kaydetme metodunun (save) tam 1 kez tetiklendiğini doğrula
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_kayitli_email_gonderildiginde_hata_mesaji_donmeli() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        User existingUser = new User();
        existingUser.setEmail(testEmail);

        // Mockito Emri: Bu e-posta veritabanında zaten var simülasyonu
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(existingUser));

        // When
        RegisterResponse response = authService.register(request);

        // Then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Email Zaten Kullanılıyor");

        // Kullanıcı zaten var olduğu için save metodunun ASLA tetiklenmediğini teyit et
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_yanlis_sifre_girildiginde_BadCredentialsException_firlatmali() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        User user = new User();
        user.setEmail(testEmail);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        // Mockito Emri: Girilen şifre veritabanındakiyle eşleşmiyor (false) dön
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        // When & Then (Aksiyon ve İstisna Doğrulaması bir arada)
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("E-posta veya şifre hatalı");
    }
}
