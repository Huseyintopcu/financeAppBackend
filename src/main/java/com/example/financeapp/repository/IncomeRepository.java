package com.example.financeapp.repository;

import com.example.financeapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long>
{
    @Query("""
    SELECT COALESCE(SUM(i.amount), 0)
    FROM Income i
    WHERE i.userEmail = :email
    AND MONTH(i.transactionDate) = :month
    AND YEAR(i.transactionDate) = :year
    """)
    double getMouthlyIncome(String email, int month, int year);

    List<Income> findByUserEmailAndTransactionDate(String userEmail, LocalDate transactionDate);

    List<Income> findByUserEmailAndTransactionDateBetween(String userEmail,LocalDate start, LocalDate end);


    @Override
    Optional<Income> findById(Long id);
}
