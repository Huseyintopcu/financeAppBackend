package com.example.financeapp.repository;

import com.example.financeapp.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long>
{
    List<Income> findByUserEmail(String userEmail);
}
