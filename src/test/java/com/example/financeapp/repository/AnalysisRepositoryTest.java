package com.example.financeapp.repository;

import com.example.financeapp.entity.Expense;
import com.example.financeapp.enums.ExpenseCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Test esnasında varsa local H2 veya test veri tabanını kullanması için
class AnalysisRepositoryTest
{

    @Autowired
    private AnalysisRepository analysisRepository;

    private final String testEmail = "user@finance.com";
    private final LocalDate startDate = LocalDate.of(2026, 6, 1);
    private final LocalDate endDate = LocalDate.of(2026, 6, 30);

    @BeforeEach
    void setUp() {
        // Her testten önce veritabanını temizle ve test verilerini ekle
        analysisRepository.deleteAll();

        Expense expense1 = new Expense();
        expense1.setCategory(ExpenseCategory.SHOPPING);
        expense1.setAmount(150.0);
        expense1.setUserEmail(testEmail);
        expense1.setTransactionDate(LocalDate.of(2026, 6, 5)); // Tarih aralığında

        Expense expense2 = new Expense();
        expense2.setCategory(ExpenseCategory.SHOPPING);
        expense2.setAmount(250.0);
        expense2.setUserEmail(testEmail);
        expense2.setTransactionDate(LocalDate.of(2026, 6, 10)); // Aynı kategoride, aralıkta

        Expense expense3 = new Expense();
        expense3.setCategory(ExpenseCategory.BILLS);
        expense3.setAmount(500.0);
        expense3.setUserEmail(testEmail);
        expense3.setTransactionDate(LocalDate.of(2026, 6, 12)); // Farklı kategori, aralıkta

        Expense expenseOtherUser = new Expense();
        expenseOtherUser.setCategory(ExpenseCategory.SHOPPING);
        expenseOtherUser.setAmount(100.0);
        expenseOtherUser.setUserEmail("other@finance.com"); // Farklı kullanıcı
        expenseOtherUser.setTransactionDate(LocalDate.of(2026, 6, 5));

        Expense expenseOtherDate = new Expense();
        expenseOtherDate.setCategory(ExpenseCategory.ENTERTAINMENT);
        expenseOtherDate.setAmount(300.0);
        expenseOtherDate.setUserEmail(testEmail);
        expenseOtherDate.setTransactionDate(LocalDate.of(2026, 7, 1)); // Tarih aralığı dışında

        analysisRepository.saveAll(List.of(expense1, expense2, expense3, expenseOtherUser, expenseOtherDate));
    }

    @Test
    void getCategoryTotals_kategorileri_ve_toplamlari_dogru_gruplamali()
    {
        // When: Sorguyu çalıştır
        List<Object[]> results = analysisRepository.getCategoryTotals(testEmail, startDate, endDate);

        // Then: Sonuçları doğrula
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(2); // Market ve Fatura olmak üzere 2 kategori gelmeli

        // Market kategorisinin toplamı 150 + 250 = 400.0 olmalı
        Object[] marketResult = results.stream()
                .filter(row -> row[0].equals(ExpenseCategory.SHOPPING))
                .findFirst()
                .orElseThrow();
        assertThat(marketResult[1]).isEqualTo(400.0);

        // Fatura kategorisinin toplamı 500.0 olmalı
        Object[] faturaResult = results.stream()
                .filter(row -> row[0].equals(ExpenseCategory.BILLS))
                .findFirst()
                .orElseThrow();
        assertThat(faturaResult[1]).isEqualTo(500.0);
    }

    @Test
    void findAllByUserEmailAndTransactionDateBetween_filtreleri_dogru_uygulamali() {
        // When: Spring Data JPA metodunu çağır
        List<Expense> expenses = analysisRepository.findAllByUserEmailAndTransactionDateBetween(testEmail, startDate, endDate);

        // Then: Sadece hedef kullanıcının ve doğru tarihteki 3 verisi gelmeli
        assertThat(expenses).hasSize(3);
        assertThat(expenses).allMatch(e -> e.getUserEmail().equals(testEmail));
        assertThat(expenses).allMatch(e -> !e.getTransactionDate().isBefore(startDate) && !e.getTransactionDate().isAfter(endDate));
    }
}
