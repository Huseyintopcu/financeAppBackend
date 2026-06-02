package com.example.financeapp.service;

import com.example.financeapp.dto.IncomeRequest;
import com.example.financeapp.dto.IncomeResponse;
import com.example.financeapp.entity.Income;
import com.example.financeapp.repository.IncomeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService
{
    private final IncomeRepository incomeRepository;

    @Transactional
    public IncomeResponse addIncome(IncomeRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Income income = new Income();
        income.setTitle(request.getTitle());
        income.setAmount(request.getAmount());
        income.setTransactionDate(request.getTransactionDate());
        income.setUserEmail(email);

        incomeRepository.save(income);

        return new IncomeResponse(true,"Gelir Eklendi");
    }

    public double getCurrentMonthIncome()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();

        return incomeRepository.getMouthlyIncome(email, now.getMonthValue(), now.getYear());
    }

    public List<Income> getALlIncome()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return incomeRepository.findByUserEmailAndTransactionDateBetweenOrderByTransactionDateDesc(email,start,end);
    }

    public void deleteIncome(long id)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Income income = incomeRepository.findById(id).orElseThrow();

        if (!income.getUserEmail().equals(email))
        {
            throw new RuntimeException("Yetkisiz işlem");
        }

        incomeRepository.delete(income);
    }
}
