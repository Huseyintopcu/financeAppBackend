package com.example.financeapp.repository;

import com.example.financeapp.entity.Income;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    private final String testEmail = "user@finance.com";
    private final LocalDate targetDate = LocalDate.of(2026, 6, 6); // Bugünün tarihi
    private final int testMonth = 6;
    private final int testYear = 2026;

    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();

        // 1. Gelir: Maaş (Haziran 2026 - Hedef kullanıcı)
        Income income1 = new Income();
        income1.setTitle("Maaş Ödemesi");
        income1.setUserEmail(testEmail);
        income1.setAmount(15000.0);
        income1.setTransactionDate(targetDate);

        // 2. Gelir: Ek Gelir (Haziran 2026 - Hedef kullanıcı)
        Income income2 = new Income();
        income2.setTitle("Freelance Gelir");
        income2.setUserEmail(testEmail);
        income2.setAmount(2500.0);
        income2.setTransactionDate(targetDate.minusDays(2)); // 4 Haziran 2026

        // 3. Gelir: Farklı ayda (Temmuz 2026 - Hedef kullanıcı)
        Income incomeOtherMonth = new Income();
        incomeOtherMonth.setTitle("Kira Geliri");
        incomeOtherMonth.setUserEmail(testEmail);
        incomeOtherMonth.setAmount(3000.0);
        incomeOtherMonth.setTransactionDate(LocalDate.of(2026, 7, 1));

        // 4. Gelir: Haziran 2026 ama FARKLI KULLANICIYA ait
        Income incomeOtherUser = new Income();
        incomeOtherUser.setTitle("Yatırım Kazancı");
        incomeOtherUser.setUserEmail("other@finance.com");
        incomeOtherUser.setAmount(4000.0);
        incomeOtherUser.setTransactionDate(targetDate);

        incomeRepository.saveAll(List.of(income1, income2, incomeOtherMonth, incomeOtherUser));
    }


    @Test
    void getMouthlyIncome_aylik_toplam_geliri_dogru_hesaplamali() {
        // When: Haziran 2026 ayı gelirlerini sorgula
        double totalIncome = incomeRepository.getMouthlyIncome(testEmail, testMonth, testYear);

        // Then: Temmuz ayı ve diğer kullanıcı elenmeli. Geriye 15000 + 2500 = 17500.0 kalmalı
        assertThat(totalIncome).isEqualTo(17500.0);
    }

    @Test
    void findByUserEmailAndTransactionDate_belirli_gundeki_gelirleri_getirmeli() {
        // When: Hedef tarihteki (6 Haziran) gelirleri sorgula
        List<Income> dailyIncomes = incomeRepository.findByUserEmailAndTransactionDate(testEmail, targetDate);

        // Then: Diğer kullanıcı elenmeli, sadece hedef kullanıcının o günkü 1 geliri gelmeli
        assertThat(dailyIncomes).hasSize(1);
        assertThat(dailyIncomes.get(0).getAmount()).isEqualTo(15000.0);
    }

    @Test
    void findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc_gelirleri_yeniden_eskiye_siralamali() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);

        // When: Haziran ayı gelirlerini sıralı getir
        List<Income> sortedIncomes = incomeRepository.findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc(testEmail, start, end);

        // Then: 2 gelir gelmeli ve ilk sırada (0. indeks) tarihi en yeni olan (6 Haziran) yer almalı
        assertThat(sortedIncomes).hasSize(2);
        assertThat(sortedIncomes.get(0).getTransactionDate()).isEqualTo(targetDate); // 6 Haziran
        assertThat(sortedIncomes.get(1).getTransactionDate()).isEqualTo(targetDate.minusDays(2)); // 4 Haziran
    }

    @Test
    void deleteByUserEmail_kullaniciya_ait_tum_gelirleri_silmeli() {
        // When: Kullanıcının gelir kayıtlarını temizle
        incomeRepository.deleteByUserEmail(testEmail);

        // Then: Geriye sadece diğer kullanıcının 1 gelir kaydı kalmalı
        List<Income> remaining = incomeRepository.findAll();
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getUserEmail()).isEqualTo("other@finance.com");
    }
}
