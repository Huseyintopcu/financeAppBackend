package com.example.financeapp.service;

import com.example.financeapp.dto.BillRequest;
import com.example.financeapp.dto.BillResponse;
import com.example.financeapp.entity.Bill;
import com.example.financeapp.repository.BillRepository;
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
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillService billService;

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
    void addBill_yeni_faturayi_kullanici_emaili_ile_basariyla_kaydetmeli() {
        // Given
        BillRequest request = new BillRequest();
        request.setTitle("İnternet");
        request.setAmount(200.0);
        request.setFinalPaymentDate(LocalDate.now());

        // When
        BillResponse response = billService.addBill(request);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Fatura eklendi");
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void deleteBill_fatura_baskasina_aitse_RuntimeException_firlatmali() {
        // Given: Başka bir kullanıcıya ait sahte bir fatura oluşturuyoruz
        Bill otherBill = new Bill();
        otherBill.setId(99L);
        otherBill.setUserEmail("baskasi@finance.com"); // Farklı kullanıcı

        when(billRepository.findById(99L)).thenReturn(Optional.of(otherBill));

        // When & Then: Metot çağrıldığında "Yetkisiz işlem" hatası fırlatmalı ve silme metoduna hiç gitmemeli
        assertThatThrownBy(() -> billService.deleteBill(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Yetkisiz işlem");

        verify(billRepository, never()).delete(any(Bill.class));
    }

    @Test
    void payBill_fatura_bulundugunda_paid_durumunu_true_yapip_kaydetmeli() {
        // Given
        Bill existingBill = new Bill();
        existingBill.setId(1L);
        existingBill.setPaid(false);

        when(billRepository.findById(1L)).thenReturn(Optional.of(existingBill));

        // When
        BillResponse response = billService.payBill(1L);

        // Then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Fatura başarıyla ödendi.");
        assertThat(existingBill.isPaid()).isTrue(); // Nesnenin durumunun değiştiğini doğrula
        verify(billRepository, times(1)).save(existingBill);
    }
}
