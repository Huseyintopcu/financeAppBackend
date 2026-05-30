package com.example.financeapp.service;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.repository.AnalysisRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalysisService
{
    private final AnalysisRepository analysisRepository;

    public  AnalysisService(AnalysisRepository analysisRepository)
    {
        this.analysisRepository = analysisRepository;
    }
    // Get a expense list which give back category and total for this week and the last week
    public List<CategoryExpenseResponse> getWeeklyAnalysis()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate thisWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisWeekEnd = thisWeekStart.plusDays(6);

        LocalDate lastWeekStart = thisWeekStart.minusDays(7);
        LocalDate lastWeekEnd = thisWeekEnd.minusDays(7);

        List<Object[]> thisWeekRows = analysisRepository.getCategoryTotals(email,thisWeekStart,thisWeekEnd);
        List<Object[]> lastWeekRows = analysisRepository.getCategoryTotals(email, lastWeekStart, lastWeekEnd);

        Map<String, Double> lastWeekMap = new HashMap<>();
        for (Object[] row : lastWeekRows)
        {
            lastWeekMap.put(row[0].toString(), ((Number) row[1]).doubleValue());
        }

        List<CategoryExpenseResponse> result = new ArrayList<>();

        for (Object[] row : thisWeekRows)
        {
            String category = row[0].toString();
            Double currentTotal = ((Number) row[1]).doubleValue();
            Double previousTotal = lastWeekMap.getOrDefault(category,0.0);

            result.add(new CategoryExpenseResponse(category, currentTotal,previousTotal));
        }

        return  result;
    }

    // Get a expense list which give back category and total for this month and the last month
    public List<CategoryExpenseResponse> getMonthlyAnalysis()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();
        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());

        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = lastMonthStart.with(TemporalAdjusters.lastDayOfMonth());

        List<Object[]> thisMontRows = analysisRepository.getCategoryTotals(email, thisMonthStart,thisMonthEnd);
        List<Object[]> lastMontRows = analysisRepository.getCategoryTotals(email, lastMonthStart, lastMonthEnd);

        Map<String, Double> lastMonthMap = new HashMap<>();
        for (Object[] row : lastMontRows)
        {
            lastMonthMap.put(row[0].toString(), ((Number) row[1]).doubleValue());
        }

        List<CategoryExpenseResponse> result = new ArrayList<>();

        for (Object[] row : thisMontRows)
        {
            String category = row[0].toString();
            Double currentTotal = ((Number) row[1]).doubleValue();
            Double previousTotal = lastMonthMap.getOrDefault(category,0.0);

            result.add(new CategoryExpenseResponse(category, currentTotal,previousTotal));
        }

        return  result;
    }

    // Get a expense list which give back category and total for the all times
    public List<CategoryExpenseResponse> getAllAnalysis()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate start = LocalDate.of(2000,1,1);
        LocalDate end = LocalDate.of(2100,1,1);

        List<Object[]> rows = analysisRepository.getCategoryTotals(email, start,end);

        List<CategoryExpenseResponse> result = new ArrayList<>();

        for (Object[] row : rows)
        {
            result.add(new CategoryExpenseResponse(row[0].toString(),((Number) row[1]).doubleValue(), 0));
        }

        return  result;
    }
}
