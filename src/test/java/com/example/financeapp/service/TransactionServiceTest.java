package com.example.financeapp.service;

import com.example.financeapp.dto.TransactionResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.entity.Income;
import com.example.financeapp.enums.ExpenseCategory;
import com.example.financeapp.repository.ExpenseRepository;
import com.example.financeapp.repository.IncomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private TransactionService transactionService;

    private final String testEmail = "user@finance.com";

    @Test
    void getTodayTransactions_bugun_hem_gelir_hem_gider_varsa_hepsini_tek_listede_birlestirmeli() {
        // Given (Hazırlık)
        LocalDate today = LocalDate.now();

        // 1. Sahte Gelir Nesnesi Oluşturuyoruz
        Income mockIncome = new Income();
        mockIncome.setTitle("Maaş");
        mockIncome.setAmount(5000.0);
        mockIncome.setTransactionDate(today);
        mockIncome.setUserEmail(testEmail);

        // 2. Sahte Gider Nesnesi Oluşturuyoruz
        Expense mockExpense = new Expense();
        mockExpense.setTitle("Market Alışverişi");
        mockExpense.setAmount(250.0);
        mockExpense.setTransactionDate(today);
        mockExpense.setCategory(ExpenseCategory.SHOPPING); // Kendi kategorinize göre güncelleyin
        mockExpense.setUserEmail(testEmail);

        // Mockito Emirleri: Repolardan bu sahte listeleri dönmesini söylüyoruz
        when(incomeRepository.findByUserEmailAndTransactionDate(eq(testEmail), any(LocalDate.class)))
                .thenReturn(List.of(mockIncome));
        when(expenseRepository.findByUserEmailAndTransactionDate(eq(testEmail), any(LocalDate.class)))
                .thenReturn(List.of(mockExpense));

        // When (Aksiyon)
        List<TransactionResponse> transactions = transactionService.getTodayTransactions(testEmail);

        // Then (Doğrulama)
        assertThat(transactions).isNotNull().hasSize(2); // Toplamda 2 hareket gelmeli

        // İlk hareket Gelir (INCOME) olmalı ve değerleri doğru map edilmeli
        TransactionResponse incomeResult = transactions.get(0);
        assertThat(incomeResult.getType()).isEqualTo("INCOME");
        assertThat(incomeResult.getTitle()).isEqualTo("Maaş");
        assertThat(incomeResult.getAmount()).isEqualTo(5000.0);

        // İkinci hareket Gider (EXPENSE) olmalı ve kategori adı string olarak doğru gelmeli
        TransactionResponse expenseResult = transactions.get(1);
        assertThat(expenseResult.getType()).isEqualTo("EXPENSE");
        assertThat(expenseResult.getTitle()).isEqualTo("Market Alışverişi");
        assertThat(expenseResult.getCategory()).isEqualTo("MARKET");
    }
}
