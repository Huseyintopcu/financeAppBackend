package com.example.financeapp.dto;

import com.example.financeapp.enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiIBillResponse
{
    private List<BillItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillItem
    {
        private String title;
        private int quantity;
        private double amount;
        private ExpenseCategory category;
    }
}
