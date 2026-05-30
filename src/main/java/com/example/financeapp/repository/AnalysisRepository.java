package com.example.financeapp.repository;

import com.example.financeapp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Expense,Long>
{
    @Query("""
    SELECT e.category, SUM(e.amount)
    FROM Expense e
    WHERE e.userEmail = :email
    AND e.transactionDate BETWEEN :startDate AND :endDate
    GROUP BY e.category
    """)
    List<Object[]> getCategoryTotals(String email, LocalDate startDate, LocalDate endDate);
}
