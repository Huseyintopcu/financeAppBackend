package com.example.financeapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpenseResponse
{
    private String category;
    private double total;
    private double previousTotal;
    private Map<Integer, Double> dailyBreakdown;
    private Map<Integer, Double> weeklyBreakdown;
}
