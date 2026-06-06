package com.example.financeapp.controller;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.entity.Income;
import com.example.financeapp.service.IncomeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ Standardı
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncomeController.class) // Sadece Web katmanını ve IncomeController'ı yükler
class IncomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // DTO nesnesini JSON string'e çevirmek için

    @MockitoBean // Spring Boot 3.4+ için kullanılan yeni yapı
    private IncomeService incomeService;

    // --- ADD INCOME TESTS ---
    @Test
    void addIncome_basarili_oldugunda_200_ok_ve_response_donmeli() throws Exception {
        IncomeRequest request = new IncomeRequest();
        request.setTitle("Maaş Ödemesi");
        request.setAmount(25000.0);
        request.setTransactionDate(LocalDate.now());

        IncomeResponse mockResponse = new IncomeResponse(true, "Gelir Eklendi");

        Mockito.when(incomeService.addIncome(any(IncomeRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/income/add")
                        .with(csrf())
                        .with(user("user@finance.com")) // Spring Security inline bypass
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Gelir Eklendi"));
    }

    // --- GET MONTHLY TOTAL TESTS ---
    @Test
    void getMontlyTotal_cagrildiginda_toplam_tutari_double_donmeli() throws Exception {
        Mockito.when(incomeService.getCurrentMonthIncome()).thenReturn(3500.0);

        mockMvc.perform(get("/income/monthly-total")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("3500.0"));
    }

    // --- GET ALL INCOME TESTS ---
    @Test
    void getAllIncome_cagrildiginda_gelir_listesi_ve_200_ok_donmeli() throws Exception {
        Income mockIncome = new Income();
        mockIncome.setId(1L);
        mockIncome.setTitle("Freelance");
        mockIncome.setAmount(4500.0);
        mockIncome.setTransactionDate(LocalDate.now());
        mockIncome.setUserEmail("user@finance.com");

        Mockito.when(incomeService.getALlIncome()).thenReturn(List.of(mockIncome));

        mockMvc.perform(get("/income/all")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.title").value("Freelance"))
                .andExpect(jsonPath("$.amount").value(4500.0));
    }

    // --- DELETE INCOME TESTS ---
    @Test
    void deleteIncome_id_gonderildiginde_void_metodu_tetiklemeli_ve_200_ok_donmeli() throws Exception {
        Mockito.doNothing().when(incomeService).deleteIncome(1L);

        mockMvc.perform(delete("/income/1")
                        .with(csrf())
                        .with(user("user@finance.com")))
                .andExpect(status().isOk());

        Mockito.verify(incomeService, Mockito.times(1)).deleteIncome(1L);
    }
}
