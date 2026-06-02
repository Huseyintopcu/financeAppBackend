package com.example.financeapp.controller;

import com.example.financeapp.dto.BillRequest;
import com.example.financeapp.dto.BillResponse;
import com.example.financeapp.entity.Bill;
import com.example.financeapp.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController
{
    private final BillService billService;

    @PostMapping("/add")
    public ResponseEntity<BillResponse> addBill (@Valid @RequestBody BillRequest request)
    {
        BillResponse response = billService.addBill(request);
        if (response.isSuccess())
        {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/monthly-list")
    public ResponseEntity<List<Bill>> getThisMonthBills()
    {
        List<Bill> response = billService.getThisMonthBills();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getThisMonthTotalAmount()
    {
        double totalAmount = billService.getThisMonthTotalAmount();
        return ResponseEntity.ok(totalAmount);
    }

    @DeleteMapping("/{id}")
    public void deleteBill(@PathVariable long id)
    {
        billService.deleteBill(id);
    }

    @GetMapping("/upcoming-critical")
    public ResponseEntity<List<Bill>> getUpcomingCriticalBills()
    {
        List<Bill> bills = billService.getUpcomingCriticalBills();

        return ResponseEntity.ok(bills);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillResponse> payBill(@PathVariable Long id)
    {
        BillResponse response = billService.payBill(id);

        if (response.isSuccess())
        {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
