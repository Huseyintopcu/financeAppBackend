package com.example.financeapp.controller;

import com.example.financeapp.dto.BillRequest;
import com.example.financeapp.dto.BillResponse;
import com.example.financeapp.entity.Bill;
import com.example.financeapp.service.BillService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillController.class) // Sadece Web katmanını ve BillController'ı yükler
class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // DTO nesnesini JSON string'e çevirmek için

    @MockitoBean // @MockBean yerine Spring Boot 3.4+ için kullanılan yeni yapı
    private BillService billService;

    // --- ADD BILL TESTS ---
    @Test
    void addBill_basarili_oldugunda_200_ok_donmeli() throws Exception {
        // DTO'nuzda AllArgsConstructor yoksa alt alta set edebilirsiniz
        BillRequest request = new BillRequest();
        request.setTitle("Elektrik Faturası");
        request.setAmount(350.0);
        request.setFinalPaymentDate(LocalDate.now());

        BillResponse mockResponse = new BillResponse(true, "Fatura eklendi");

        Mockito.when(billService.addBill(any(BillRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/bills/add")
                        .with(csrf())
                        .with(user("user@finance.com")) // Spring Security inline bypass
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fatura eklendi"));
    }

    // --- GET MONTHLY LIST TESTS ---
    @Test
    void getThisMonthBills_cagrildiginda_fatura_listesi_ve_200_ok_donmeli() throws Exception {
        Bill mockBill = new Bill();
        mockBill.setId(1L);
        mockBill.setTitle("Su Faturası");
        mockBill.setAmount(150.0);
        mockBill.setUserEmail("user@finance.com");

        Mockito.when(billService.getThisMonthBills()).thenReturn(List.of(mockBill));

        mockMvc.perform(get("/bills/monthly-list")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Su Faturası"))
                .andExpect(jsonPath("$[0].amount").value(150.0));
    }

    // --- GET TOTAL AMOUNT TESTS ---
    @Test
    void getThisMonthTotalAmount_cagrildiginda_toplam_tutari_double_donmeli() throws Exception {
        Mockito.when(billService.getThisMonthTotalAmount()).thenReturn(500.0);

        mockMvc.perform(get("/bills/total")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }

    // --- DELETE BILL TESTS ---
    @Test
    void deleteBill_id_gonderildiginde_void_metodu_tetiklemeli_ve_200_ok_donmeli() throws Exception {
        Mockito.doNothing().when(billService).deleteBill(1L);

        mockMvc.perform(delete("/bills/1")
                        .with(csrf())
                        .with(user("user@finance.com")))
                .andExpect(status().isOk());

        Mockito.verify(billService, Mockito.times(1)).deleteBill(1L);
    }

    // --- GET UPCOMING CRITICAL TESTS ---
    @Test
    void getUpcomingCriticalBills_cagrildiginda_kritik_fatura_listesini_donmeli() throws Exception {
        Bill criticalBill = new Bill();
        criticalBill.setTitle("İnternet");
        criticalBill.setAmount(200.0);

        Mockito.when(billService.getUpcomingCriticalBills()).thenReturn(List.of(criticalBill));

        mockMvc.perform(get("/bills/upcoming-critical")
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("İnternet"));
    }

    // --- PUT PAY BILL TESTS ---
    @Test
    void payBill_basarili_oldugunda_200_ok_donmeli() throws Exception {
        BillResponse mockResponse = new BillResponse(true, "Fatura başarıyla ödendi.");

        Mockito.when(billService.payBill(1L)).thenReturn(mockResponse);

        mockMvc.perform(put("/bills/1/pay")
                        .with(csrf())
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Fatura başarıyla ödendi."));
    }

    @Test
    void payBill_fatura_bulunamadiginda_400_bad_request_donmeli() throws Exception {
        BillResponse mockResponse = new BillResponse(false, "Fatura bulunamadı.");

        Mockito.when(billService.payBill(99L)).thenReturn(mockResponse);

        mockMvc.perform(put("/bills/99/pay")
                        .with(csrf())
                        .with(user("user@finance.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Controller'daki HttpStatus.BAD_REQUEST kontrolü
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Fatura bulunamadı."));
    }
}
