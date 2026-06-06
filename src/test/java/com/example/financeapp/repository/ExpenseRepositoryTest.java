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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    private final String testEmail = "user@finance.com";
    private final LocalDate targetDate = LocalDate.of(2026, 6, 6); // Bugünün tarihi
    private final int testMonth = 6;
    private final int testYear = 2026;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();

        // 1. Harcama: Bugünün tarihinde, hedef kullanıcıya ait
        Expense expense1 = new Expense();
        expense1.setTitle("Market Alışverişi"); // EKLENDİ (nullable = false için)
        expense1.setQuantity(1);                // EKLENDİ (nullable = false için)
        expense1.setUserEmail(testEmail);
        expense1.setAmount(120.0);
        expense1.setTransactionDate(targetDate);
        expense1.setCategory(ExpenseCategory.SHOPPING);

        // 2. Harcama: Hedef kullanıcıya ait, 2 gün önce
        Expense expense2 = new Expense();
        expense2.setTitle("Elektrik Faturası");
        expense2.setQuantity(1);
        expense2.setUserEmail(testEmail);
        expense2.setAmount(80.0);
        expense2.setTransactionDate(targetDate.minusDays(2));
        expense2.setCategory(ExpenseCategory.BILLS);

        // 3. Harcama: Farklı ayda (Temmuz), hedef kullanıcıya ait
        Expense expenseOtherMonth = new Expense();
        expenseOtherMonth.setTitle("Mutfak Alışverişi");
        expenseOtherMonth.setQuantity(3);
        expenseOtherMonth.setUserEmail(testEmail);
        expenseOtherMonth.setAmount(500.0);
        expenseOtherMonth.setTransactionDate(LocalDate.of(2026, 7, 1));
        expenseOtherMonth.setCategory(ExpenseCategory.SHOPPING);

        // 4. Harcama: Bugünün tarihinde ama FARKLI KULLANICIYA ait
        Expense expenseOtherUser = new Expense();
        expenseOtherUser.setTitle("Hediye");
        expenseOtherUser.setQuantity(1);
        expenseOtherUser.setUserEmail("other@finance.com");
        expenseOtherUser.setAmount(300.0);
        expenseOtherUser.setTransactionDate(targetDate);
        expenseOtherUser.setCategory(ExpenseCategory.SHOPPING);

        expenseRepository.saveAll(List.of(expense1, expense2, expenseOtherMonth, expenseOtherUser));
    }


    @Test
    void getMonthlyExpense_aylik_toplam_harcamayi_hesaplamali() {
        // When: Haziran 2026 ayı harcamalarını getir
        double totalExpense = expenseRepository.getMonthlyExpense(testEmail, testMonth, testYear);

        // Then: Temmuz ayı ve diğer kullanıcı elenmeli. Geriye 120 + 80 = 200.0 kalmalı
        assertThat(totalExpense).isEqualTo(200.0);
    }

    @Test
    void findByUserEmailAndTransactionDate_bugunun_harcamalarini_getirmeli() {
        // When: Bugünün harcamalarını sorgula
        List<Expense> todayExpenses = expenseRepository.findByUserEmailAndTransactionDate(testEmail, targetDate);

        // Then: Sadece hedef kullanıcının bugünkü harcaması gelmeli (expense1)
        assertThat(todayExpenses).hasSize(1);
        assertThat(todayExpenses.get(0).getAmount()).isEqualTo(120.0);
    }

    @Test
    void findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc_tarihe_gore_yeniden_eskiye_siralamali() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);

        // When: Haziran ayı harcamalarını sıralı getir
        List<Expense> sortedExpenses = expenseRepository.findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc(testEmail, start, end);

        // Then: 2 harcama gelmeli ve ilk sırada tarihi daha yeni olan (6 Haziran olan) yer almalı
        assertThat(sortedExpenses).hasSize(2);
        assertThat(sortedExpenses.get(0).getTransactionDate()).isEqualTo(targetDate); // 6 Haziran
        assertThat(sortedExpenses.get(1).getTransactionDate()).isEqualTo(targetDate.minusDays(2)); // 4 Haziran
    }

    @Test
    void deleteByUserEmail_kullaniciya_ait_tum_harcamalari_silmeli() {
        // When: Kullanıcının harcamalarını temizle
        expenseRepository.deleteByUserEmail(testEmail);

        // Then: Geriye sadece diğer kullanıcının  harcaması kalmalı
        List<Expense> remaining = expenseRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getUserEmail()).isEqualTo("other@finance.com");
    }
}
