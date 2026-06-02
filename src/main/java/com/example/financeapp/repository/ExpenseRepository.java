package com.example.financeapp.repository;


import com.example.financeapp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long>
{
    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM Expense e
    WHERE e.userEmail = :email
    AND MONTH(e.transactionDate) = :month
    AND YEAR(e.transactionDate) = :year
    """)
    double getMonthlyExpense(String email, int month, int year);

    List<Expense> findByUserEmailAndTransactionDate(String email, LocalDate today);

    List<Expense> findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc(String email,LocalDate start,LocalDate end);

    Optional<Expense> findById(long id);

}
