package com.example.financeapp.repository;

import com.example.financeapp.entity.Bill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long>
{
    @Query("""
    SELECT COALESCE(SUM(e.amount),0)
    FROM Bill e
    WHERE e.userEmail = :email
    AND MONTH(e.finalPaymentDate) = :month
    AND YEAR(e.finalPaymentDate) = :year
    """)
    double getThisMonthTotalAmount(String email, int month, int year);

    List<Bill> findByUserEmailAndFinalPaymentDateBetween(String email, LocalDate thisMonthStart, LocalDate thisMonthEnd);

    Optional<Bill> findById(long id);

    List<Bill> findByIsPaidFalseAndFinalPaymentDateBetween(LocalDate today, LocalDate maxTargetDate);

    List<Bill> findByUserEmailAndIsPaidFalseAndFinalPaymentDateBetweenOrderByFinalPaymentDateAsc(String userEmail, LocalDate start, LocalDate end);

    @Transactional
    void deleteByUserEmail(String email);
}
