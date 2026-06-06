package com.example.financeapp.service;

import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.enums.ExpenseCategory;
import com.example.financeapp.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

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
    void addExpense_yeni_gideri_aktif_kullanici_emaili_ile_basariyla_kaydetmeli() {
        // Given (Hazırlık)
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Market");
        request.setAmount(150.0);
        request.setQuantity(1);
        request.setCategory(ExpenseCategory.FOOD); // Kendi kategorinize göre güncelleyin

        // When (Aksiyon)
        ExpenseResponse response = expenseService.addExpense(request);

        // Then (Doğrulama)
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Gider Eklendi");

        // Veritabanına kaydetme metodunun tam 1 kez tetiklendiğini kesinleştir
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void deleteExpense_gider_baskasina_aitse_RuntimeException_firlatmali() {
        // Given: Başka bir kullanıcıya ait sahte bir gider kaydı oluşturuyoruz
        Expense otherExpense = new Expense();
        otherExpense.setId(55L);
        otherExpense.setUserEmail("baskasi@finance.com"); // Farklı kullanıcı e-postası

        when(expenseRepository.findById(55L)).thenReturn(Optional.of(otherExpense));

        // When & Then: Metot çağrıldığında "Yetkisiz işlem" hatası fırlatmalı ve silme aksiyonuna hiç geçmemeli
        assertThatThrownBy(() -> expenseService.deleteExpense(55L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Yetkisiz işlem");

        // Veritabanından silme metodunun ASLA çalıştırılmadığını doğrula
        verify(expenseRepository, never()).delete(any(Expense.class));
    }
}
