package com.example.financeapp.controller;

import com.example.financeapp.dto.ExpenseRequest;
import com.example.financeapp.dto.ExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.enums.ExpenseCategory;
import com.example.financeapp.service.ExpenseService;
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

@WebMvcTest(ExpenseController.class) // Sadece Web katmanını ve ExpenseController'ı yükler
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // DTO nesnesini JSON string'e çevirmek için

    @MockitoBean // Spring Boot 3.4+ için kullanılan yeni yapı
    private ExpenseService expenseService;

    // --- ADD EXPENSE TESTS ---
    @Test
    void addExpense_basarili_oldugunda_200_ok_ve_response_donmeli() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Market Alışverişi");
        request.setAmount(150.0);
        request.setQuantity(1);
        request.setCategory(ExpenseCategory.FOOD);

        ExpenseResponse mockResponse = new ExpenseResponse(true, "Gider Eklendi");

        Mockito.when(expenseService.addExpense(any(ExpenseRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/expense/add")
                        .with(csrf())
                        .with(user("user@finance.com")) // Spring Security inline bypass
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Gider Eklendi"));
    }

    // --- GET MONTHLY TOTAL TESTS ---
    @Test
    void getMontlyExpense_cagrildiginda_toplam_tutari_double_donmeli() throws Exception {
        Mockito.when(expenseService.getMonthlyExpense()).thenReturn(1450.50);

        mockMvc.perform(get("/expense/monthly-total")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1450.50"));
    }

    // --- GET ALL EXPENSE TESTS ---
    @Test
    void gelAllExpense_cagrildiginda_gider_listesi_ve_200_ok_donmeli() throws Exception {
        Expense mockExpense = new Expense();
        mockExpense.setId(1L);
        mockExpense.setTitle("Taksi");
        mockExpense.setAmount(80.0);
        mockExpense.setQuantity(1);
        mockExpense.setCategory(ExpenseCategory.TRANSPORT);
        mockExpense.setTransactionDate(LocalDate.now());
        mockExpense.setUserEmail("user@finance.com");

        Mockito.when(expenseService.getAllExpense()).thenReturn(List.of(mockExpense));

        mockMvc.perform(get("/expense/all")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.title").value("Taksi"))
                .andExpect(jsonPath("$.amount").value(80.0));
    }

    // --- DELETE EXPENSE TESTS ---
    @Test
    void deleteExpense_id_gonderildiginde_void_metodu_tetiklemeli_ve_200_ok_donmeli() throws Exception {
        Mockito.doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/expense/1")
                        .with(csrf())
                        .with(user("user@finance.com")))
                .andExpect(status().isOk());

        Mockito.verify(expenseService, Mockito.times(1)).deleteExpense(1L);
    }
}
