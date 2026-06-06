package com.example.financeapp.service;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.enums.ExpenseCategory;
import com.example.financeapp.repository.AnalysisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private AnalysisService analysisService;

    private final String testEmail = "user@finance.com";

    @BeforeEach
    void setUp() {
        // Spring Security Context yapısını test ortamında sahte bir kullanıcı ile simüle ediyoruz (Mocking SecurityContext)
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(testEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllAnalysis_tum_zamanlar_verisini_dogru_donusturmeli() {
        // Given: Depodan dönecek sahte Object array listesini hazırla
        Object[] row = new Object[]{"FOOD", 1250.50};
        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);

        Mockito.doReturn(mockResult).when(analysisRepository).getCategoryTotals(eq(testEmail), any(LocalDate.class), any(LocalDate.class));



        // When: Servis metodunu çağır
        List<CategoryExpenseResponse> responses = analysisService.getAllAnalysis();

        // Then: Sonuçları doğrula
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCategory()).isEqualTo("FOOD");
        assertThat(responses.get(0).getTotal()).isEqualTo(1250.50);
        assertThat(responses.get(0).getPreviousTotal()).isEqualTo(0.0);
    }

    @Test
    void getWeeklyAnalysis_haftalik_karsilastirmayi_ve_gunluk_kirilimi_hesaplamali() {
        // Given: Bu hafta ve geçen hafta için dönecek sahte verileri hazırla
        Object[] currentWeekRow = new Object[]{"TRANSPORT", 150.0, 1};
        Object[] lastWeekRow = new Object[]{"TRANSPORT", 100.0, 3};

        List<Object[]> mockCurrentWeek = new ArrayList<Object[]>();
        mockCurrentWeek.add(currentWeekRow);

        List<Object[]> mockLastWeek = new ArrayList<Object[]>();
        mockLastWeek.add(lastWeekRow);

        // doReturn yapısında peş peşe çağrılar virgül ile ayrılır:
        Mockito.doReturn(mockCurrentWeek, mockLastWeek).when(analysisRepository)
                .getCategoryTotalsWithDays(eq(testEmail), any(LocalDate.class), any(LocalDate.class));

        // When: Servis metodunu tetikle
        List<CategoryExpenseResponse> responses = analysisService.getWeeklyAnalysis();

        // Then: Verilerin doğru eşleştiğini ve hesaplandığını doğrula
        assertThat(responses).hasSize(1);
        CategoryExpenseResponse result = responses.get(0);

        assertThat(result.getCategory()).isEqualTo("TRANSPORT");
        assertThat(result.getTotal()).isEqualTo(150.0);
        assertThat(result.getPreviousTotal()).isEqualTo(100.0);
        assertThat(result.getDailyBreakdown()).containsKey(1);
        assertThat(result.getDailyBreakdown().get(1)).isEqualTo(150.0);
    }
}
