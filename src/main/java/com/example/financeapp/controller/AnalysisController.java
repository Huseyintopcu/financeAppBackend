package com.example.financeapp.controller;

import com.example.financeapp.dto.CategoryExpenseResponse;
import com.example.financeapp.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController
{
    private final AnalysisService analysisService;

    @GetMapping("/weekly")
    public ResponseEntity<List<CategoryExpenseResponse>> weekly()
    {
        List<CategoryExpenseResponse> analysisList = analysisService.getWeeklyAnalysis();
        if (analysisList == null || analysisList.isEmpty())
        {
            throw new RuntimeException("Bu haftaya ait herhangi bir harcama analizi oluşturulamadı.");
        }
        return ResponseEntity.ok(analysisList);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<CategoryExpenseResponse>> monthly()
    {
        List<CategoryExpenseResponse> analysisList = analysisService.getMonthlyAnalysis();
        if (analysisList == null || analysisList.isEmpty())
        {
            throw new RuntimeException("Bu aya ait herhangi bir harcama analizi oluşturulamadı.");
        }
        return ResponseEntity.ok(analysisList);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryExpenseResponse>>  all()
    {
        List<CategoryExpenseResponse> analysisList = analysisService.getAllAnalysis();
        if (analysisList == null || analysisList.isEmpty())
        {
            throw new RuntimeException("Henüz bir harcama analizi oluşturulamadı.");
        }
        return ResponseEntity.ok(analysisList);
    }
}
