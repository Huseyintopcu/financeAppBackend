package com.example.financeapp.controller;

import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ Standardı
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class) // Sadece Web katmanını ve TransactionController'ı yükler
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // @MockBean yerine Spring Boot 3.4+ için kullanılan yeni yapı
    private TransactionService transactionService;

    @Test
    void getTodayTransactions_istek_atildiginda_200_ok_ve_hareket_listesi_donmeli() throws Exception {
        // Given (Hazırlık)
        String testEmail = "user@finance.com";

        // Mockito testi için sahte bir TransactionResponse DTO nesnesi oluşturuyoruz
        TransactionResponse mockResponse = new TransactionResponse();
        mockResponse.setType("INCOME");
        mockResponse.setTitle("Maaş Ödemesi");
        mockResponse.setAmount(5000.0);
        mockResponse.setCategory("INCOME");
        mockResponse.setTransactionDate(LocalDate.now());

        // Servis katmanı çağrıldığında bu listeyi dönmesini söylüyoruz
        Mockito.when(transactionService.getTodayTransactions(eq(testEmail)))
                .thenReturn(List.of(mockResponse));

        // When & Then (Aksiyon ve HTTP Yanıt Doğrulaması)
        mockMvc.perform(get("/transactions/today") // @RequestMapping("/transactions") + @GetMapping("/today")
                        .with(user(testEmail)) // SecurityContextHolder'daki principal nesnesine bu email bilgisini enjekte eder
                        .contentType(MediaType.APPLICATION_JSON))
                // HTTP Durum kodunun 200 OK olduğunu doğrula
                .andExpect(status().isOk())
                // Dönen JSON içeriğindeki dizi ve alan yapısını denetle
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.title").value("Maaş Ödemesi"))
                .andExpect(jsonPath("$.amount").value(5000.0));
    }
}
