package com.example.financeapp.repository;

import com.example.financeapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long>
{
    List<Income> findByUserEmail(String userEmail);

    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Income i
    WHERE i.userEmail = :email
    AND MONTH(i.transactionDate) = :month
    AND YEAR(i.transactionDate) = :year
    """)
    double getMouthlyIncome(String email, int month, int year);

    List<Income> findByUserEmailAndTransactionDate(String userEmail, LocalDate transactionDate);
}
