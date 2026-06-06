package com.example.financeapp.controller;

import com.example.financeapp.dto.AiIBillResponse;
import com.example.financeapp.enums.ExpenseCategory; // Kategori enum importu
import com.example.financeapp.service.AiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ İÇİN YENİ MOCK KÜTÜPHANESİ
import org.springframework.test.web.servlet.MockMvc;

// SECURITY VE CSRF İÇİN GEREKLİ EKSİK IMPORTLAR:
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 3.4 ve sonrasında @MockBean YERİNE artık kesinlikle @MockitoBean kullanılır!
    @MockitoBean
    private AiService aiService;

    @Test
    void processBill_gorsel_yuklendiginde_200_ok_ve_yapay_zeka_verilerini_donmeli() throws Exception {
        // Given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "bill.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "sahte_gorsel_icerigi".getBytes()
        );

        AiIBillResponse.BillItem mockItem = new AiIBillResponse.BillItem();
        mockItem.setTitle("Süt");
        mockItem.setQuantity(2);
        mockItem.setAmount(45.0);
        mockItem.setCategory(ExpenseCategory.FOOD);

        AiIBillResponse mockResponse = new AiIBillResponse();
        mockResponse.setItems(Collections.singletonList(mockItem));

        Mockito.when(aiService.analyzeBillWithAi(any())).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(multipart("/api/ai/process")
                        .file(mockFile)
                        .with(csrf())
                        .with(user("user@finance.com"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].title").value("Süt"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].amount").value(45.0))
                .andExpect(jsonPath("$.items[0].category").value("FOOD"));
    }
}
