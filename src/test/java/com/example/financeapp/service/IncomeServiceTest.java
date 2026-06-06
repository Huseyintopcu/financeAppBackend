package com.example.financeapp.service;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.entity.Income;
import com.example.financeapp.repository.IncomeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @InjectMocks
    private IncomeService incomeService;

    private final String testEmail = "user@finance.com";

    @BeforeEach
    void setUp() {
        // Spring Security Context yapısını test ortamında sahte bir kullanıcı ile simüle ediyoruz
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(testEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void addIncome_yeni_geliri_aktif_kullanici_emaili_ile_basariyla_kaydetmeli() {
        // Given (Hazırlık)
        IncomeRequest request = new IncomeRequest();
        request.setTitle("Maaş");
        request.setAmount(25000.0);
        request.setTransactionDate(LocalDate.now());

        // When (Aksiyon)
        IncomeResponse response = incomeService.addIncome(request);

        // Then (Doğrulama)
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Gelir Eklendi");

        // Veritabanına kaydetme metodunun tam 1 kez tetiklendiğini kesinleştir
        verify(incomeRepository, times(1)).save(any(Income.class));
    }

    @Test
    void deleteIncome_gelir_baskasina_aitse_RuntimeException_firlatmali() {
        // Given: Başka bir kullanıcıya ait sahte bir gelir kaydı oluşturuyoruz
        Income otherIncome = new Income();
        otherIncome.setId(77L);
        otherIncome.setUserEmail("baskasi@finance.com"); // Farklı kullanıcı e-postası

        when(incomeRepository.findById(77L)).thenReturn(Optional.of(otherIncome));

        // When & Then: Metot çağrıldığında "Yetkisiz işlem" hatası fırlatmalı ve silme aksiyonuna geçmemeli
        assertThatThrownBy(() -> incomeService.deleteIncome(77L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Yetkisiz işlem");

        // Veritabanından silme metodunun ASLA çalıştırılmadığını doğrula
        verify(incomeRepository, never()).delete(any(Income.class));
    }
}
