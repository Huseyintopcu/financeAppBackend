package com.example.financeapp.repository;

import com.example.financeapp.entity.Bill;
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
class BillRepositoryTest {

    @Autowired
    private BillRepository billRepository;

    private final String testEmail = "user@finance.com";

    // Testleri dinamik kılmak için Haziran 2026 tarihlerini kullanıyoruz
    private final LocalDate targetDate = LocalDate.of(2026, 6, 15);
    private final int testMonth = 6;
    private final int testYear = 2026;

    @BeforeEach
    void setUp() {
        billRepository.deleteAll();

        // 1. Fatura: Hedef ayda, hedef kullanıcıya ait, ÖDENMEMİŞ
        Bill bill1 = new Bill();
        bill1.setTitle("Elektrik Faturası");
        bill1.setUserEmail(testEmail);
        bill1.setAmount(150.0);
        bill1.setFinalPaymentDate(targetDate);
        bill1.setPaid(false);

        // 2. Fatura: Hedef ayda, hedef kullanıcıya ait, ÖDENMİŞ
        Bill bill2 = new Bill();
        bill2.setTitle("Su Faturası");
        bill2.setUserEmail(testEmail);
        bill2.setAmount(250.0);
        bill2.setFinalPaymentDate(targetDate.plusDays(2));
        bill2.setPaid(true);

        // 3. Fatura: Farklı ayda (Temmuz), hedef kullanıcıya ait
        Bill billOtherMonth = new Bill();
        billOtherMonth.setTitle("İnternet Faturası");
        billOtherMonth.setUserEmail(testEmail);
        billOtherMonth.setAmount(500.0);
        billOtherMonth.setFinalPaymentDate(LocalDate.of(2026, 7, 5));
        billOtherMonth.setPaid(false);

        // 4. Fatura: Hedef ayda ama FARKLI KULLANICIYA ait
        Bill billOtherUser = new Bill();
        billOtherUser.setTitle("Doğalgaz Faturası");
        billOtherUser.setUserEmail("other@finance.com");
        billOtherUser.setAmount(300.0);
        billOtherUser.setFinalPaymentDate(targetDate);
        billOtherUser.setPaid(false);

        billRepository.saveAll(List.of(bill1, bill2, billOtherMonth, billOtherUser));
    }


    @Test
    void getThisMonthTotalAmount_belirtilen_ay_ve_yildaki_toplam_tutari_hesaplamali() {
        // When: Haziran 2026 için toplam tutarı sorgula
        double totalAmount = billRepository.getThisMonthTotalAmount(testEmail, testMonth, testYear);

        // Then: Sadece hedef kullanıcının Haziran ayındaki faturaları toplanmalı (150 + 250 = 400.0)
        assertThat(totalAmount).isEqualTo(400.0);
    }

    @Test
    void getThisMonthTotalAmount_veri_yoksa_sıfır_donmeli() {
        // When: Veritabanında hiç faturası olmayan bir ayı veya kullanıcıyı sorgula
        double totalAmount = billRepository.getThisMonthTotalAmount("olmayan@user.com", 1, 2026);

        // Then: COALESCE fonksiyonu sayesinde çökmeden 0.0 dönmeli
        assertThat(totalAmount).isEqualTo(0.0);
    }

    @Test
    void findByUserEmailAndIsPaidFalseAndFinalPaymentDateBetweenOrderByFinalPaymentDateAsc_odenecekleri_sirali_getirmeli() {
        LocalDate start = LocalDate.of(2026, 6, 1);
        LocalDate end = LocalDate.of(2026, 6, 30);

        // When: Haziran ayındaki ödenmemiş faturaları getir
        List<Bill> unpaidBills = billRepository.findByUserEmailAndIsPaidFalseAndFinalPaymentDateBetweenOrderByFinalPaymentDateAsc(
                testEmail, start, end
        );

        // Then: 4 faturadan sadece 1 tanesi hem bu kullanıcıya ait, hem Haziran'da, hem de ÖDENMEMİŞ (bill1)
        assertThat(unpaidBills).hasSize(1);
        assertThat(unpaidBills.get(0).getAmount()).isEqualTo(150.0);
        assertThat(unpaidBills.get(0).isPaid()).isFalse();
    }

    @Test
    void deleteByUserEmail_kullaniciya_ait_tum_faturalari_silmeli() {
        // When: Test kullanıcısının faturalarını sil
        billRepository.deleteByUserEmail(testEmail);

        // Then: Geriye sadece diğer kullanıcıya ait olan 1 fatura kalmalı
        List<Bill> allRemainingBills = billRepository.findAll();
        assertThat(allRemainingBills).hasSize(1);
        assertThat(allRemainingBills.get(0).getUserEmail()).isEqualTo("other@finance.com");
    }
}
