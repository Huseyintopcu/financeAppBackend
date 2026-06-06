package com.example.financeapp.controller;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.service.AnalysisService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ Standardı
import org.springframework.test.web.servlet.MockMvc;

// Statik Güvenlik ve İstek Yapılandırıcıları
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;

@WebMvcTest(AnalysisController.class) // Sadece Web katmanını ve AnalysisController'ı yükler
class AnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Eski @MockBean yerine Spring Boot 3.4+ için doğrusu
    private AnalysisService analysisService;

    @Test
    void all_analiz_verisi_varsa_200_ok_ve_json_listesi_donmeli() throws Exception {
        // Given (Hazırlık)
        CategoryExpenseResponse mockResponse = new CategoryExpenseResponse(
                "FOOD", 1250.0, 1000.0, new HashMap<>(), new HashMap<>()
        );

        // Servis çağrıldığında içi dolu sahte bir liste dönmesini emrediyoruz
        Mockito.when(analysisService.getAllAnalysis()).thenReturn(List.of(mockResponse));

        // When & Then (Aksiyon ve HTTP Yanıt Doğrulaması)
        mockMvc.perform(get("/analysis/all") // @RequestMapping("/analysis") + @GetMapping("/all")
                        .with(csrf()) // Spring Security CSRF koruması için
                        .with(user("user@finance.com")) // Bahsettiğimiz kararlı inline kullanıcı simülasyonu
                        .contentType(MediaType.APPLICATION_JSON))
                // HTTP Durum kodunun 200 OK olduğunu doğrula
                .andExpect(status().isOk())
                // Dönen JSON içeriğindeki düğüm yapısını denetle
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("FOOD"))
                .andExpect(jsonPath("$[0].total").value(1250.0))
                .andExpect(jsonPath("$[0].previousTotal").value(1000.0));
    }

    @Test
    void all_analiz_verisi_bos_geldiginde_500_hata_kodu_verip_RuntimeException_firlatmali() throws Exception {
        // Given: Servis boş liste dönüyor simülasyonu (Controller içindeki if bloğunu test ediyoruz)
        Mockito.when(analysisService.getAllAnalysis()).thenReturn(List.of());

        // When & Then: İstek atıldığında fırlatılan RuntimeException sonucu HTTP 500 (Internal Server Error) bekliyoruz
        mockMvc.perform(get("/analysis/all")
                        .with(csrf())
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // 500 Hata Kodu
    }
}
