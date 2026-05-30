package com.example.financeapp.controller;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.service.AnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController
{
    private AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService)
    {
        this.analysisService = analysisService;
    }

    @GetMapping("/weekly")
    public List<CategoryExpenseResponse> weekly()
    {
        return analysisService.getWeeklyAnalysis();
    }

    @GetMapping("/monthly")
    public List<CategoryExpenseResponse> monthly()
    {
        return analysisService.getMonthlyAnalysis();
    }

    @GetMapping("/all")
    public List<CategoryExpenseResponse> all()
    {
        return analysisService.getAllAnalysis();
    }
}
