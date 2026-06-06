package com.example.financeapp.service;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.entity.Expense;
import com.example.financeapp.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalysisService
{
    private final AnalysisRepository analysisRepository;

    // Get a expense list which give back category and total for this week and the last week
    public List<CategoryExpenseResponse> getWeeklyAnalysis()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate thisWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate thisWeekEnd = thisWeekStart.plusDays(6);

        LocalDate lastWeekStart = thisWeekStart.minusDays(7);
        LocalDate lastWeekEnd = thisWeekEnd.minusDays(7);

        List<Object[]> thisWeekRows = analysisRepository.getCategoryTotalsWithDays(email,thisWeekStart,thisWeekEnd);
        List<Object[]> lastWeekRows = analysisRepository.getCategoryTotalsWithDays(email, lastWeekStart, lastWeekEnd);

        Map<String, Double> lastWeekMap = new HashMap<>();
        for (Object[] row : lastWeekRows)
        {
            lastWeekMap.put(row[0].toString(), ((Number) row[1]).doubleValue());
        }

        Map<String, Double> currentTotals = new HashMap<>();
        Map<String, Map<Integer, Double>> breakdowns = new HashMap<>();


        for (Object[] row : thisWeekRows)
        {
            String category = row[0].toString();
            Double amount = ((Number) row[1]).doubleValue();
            Integer dayOfWeek = ((Number) row[2]).intValue();

            currentTotals.put(category,currentTotals.getOrDefault(category,0.0) + amount);

            breakdowns.putIfAbsent(category, new HashMap<>());
            breakdowns.get(category).put(dayOfWeek,amount);
        }

        List<CategoryExpenseResponse> result = new ArrayList<>();
        for (String category : currentTotals.keySet())
        {
            double total = currentTotals.get(category);
            double previousTotal = lastWeekMap.getOrDefault(category, 0.0);
            Map<Integer, Double> dailyBreakdown = breakdowns.getOrDefault(category, new HashMap<>());

            result.add(new CategoryExpenseResponse(category,total,previousTotal,dailyBreakdown, null));
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

        List<Expense> thisMontRows = analysisRepository.findAllByUserEmailAndTransactionDateBetween(email, thisMonthStart, thisMonthEnd);
        List<Object[]> lastMontRows = analysisRepository.getCategoryTotals(email, lastMonthStart, lastMonthEnd);

        Map<String, Double> lastMonthMap = new HashMap<>();
        for (Object[] row : lastMontRows)
        {
            lastMonthMap.put(row[0].toString(), ((Number) row[1]).doubleValue());
        }

        Map<String, Double> currentTotals = new HashMap<>();
        Map<String,Map<Integer,Double>> weeklyBreakdowns = new HashMap<>();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        for (Expense e : thisMontRows)
        {
            String category = e.getCategory().name();
            Double amount = e.getAmount();

            int weekOfMonth  = e.getTransactionDate().get(weekFields.weekOfMonth());

            currentTotals.put(category,currentTotals.getOrDefault(category, 0.0 ) + amount);

            weeklyBreakdowns.putIfAbsent(category, new HashMap<>());
            weeklyBreakdowns.get(category).put(weekOfMonth,weeklyBreakdowns.get(category).getOrDefault(weekOfMonth, 0.0) + amount);
        }

        List<CategoryExpenseResponse> result = new ArrayList<>();

        for (String category : currentTotals.keySet())
        {
           Double total = currentTotals.get(category);
           Double previousTotal = lastMonthMap.getOrDefault(category, 0.0);
           Map<Integer, Double> weeklyBreakdown = weeklyBreakdowns.getOrDefault(category, new HashMap<>());

           result.add(new CategoryExpenseResponse(category, total,previousTotal,null,weeklyBreakdown));
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
            result.add(new CategoryExpenseResponse(row[0].toString(),((Number) row[1]).doubleValue(), 0,null,null));
        }

        return  result;
    }
}
