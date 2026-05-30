package com.example.financeapp.repository;

import com.example.financeapp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Expense,Long> {
    @Query("""

            SELECT e.category, SUM(e.amount)
    FROM Expense e
    WHERE e.userEmail = :email
    AND e.transactionDate BETWEEN :startDate AND :endDate
    GROUP BY e.category
    """)
    List<Object[]> getCategoryTotals(String email, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT e.category, SUM(e.amount), CAST(EXTRACT(ISODOW FROM e.transaction_date) AS INTEGER) " +
            "FROM expenses e " +
            "WHERE e.user_email = :email AND e.transaction_date BETWEEN :start AND :end " +
            "GROUP BY e.category, EXTRACT(ISODOW FROM e.transaction_date)",
            nativeQuery = true)
    List<Object[]> getCategoryTotalsWithDays(@Param("email") String email, @Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Expense> findAllByUserEmailAndTransactionDateBetween(String email, LocalDate thisMonthStart, LocalDate thisMonthEnd);
}