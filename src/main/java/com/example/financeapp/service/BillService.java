package com.example.financeapp.service;

import com.example.financeapp.dto.BillRequest;
import com.example.financeapp.dto.BillResponse;
import com.example.financeapp.entity.Bill;
import com.example.financeapp.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService
{
    private final BillRepository billRepository;

    // Add new bill
    public BillResponse addBill(BillRequest request)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Bill bill = new Bill();
        bill.setTitle(request.getTitle());
        bill.setAmount(request.getAmount());
        bill.setFinalPaymentDate(request.getFinalPaymentDate());
        bill.setUserEmail(email);

        billRepository.save(bill);

        return  new BillResponse(true,"Fatura eklendi");
    }

    // Get a bill list for the this month
    public List<Bill> getThisMonthBills()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return billRepository.findByUserEmailAndFinalPaymentDateBetween(email, start , end);
    }

    // Get this month bills total amount
    public double getThisMonthTotalAmount()
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate now = LocalDate.now();

        return billRepository.getThisMonthTotalAmount(email, now.getMonthValue(), now.getYear());
    }

    // Delete a bill
    public void deleteBill(long id)
    {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        Bill bill = billRepository.findById(id).orElseThrow();

        if (!bill.getUserEmail().equals(email))
        {
            throw new RuntimeException("Yetkisiz işlem");
        }

        billRepository.delete(bill);
    }

    // Get bills list of left less then 4 day for final payment day
    public List<Bill> getUpcomingCriticalBills()
    {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        LocalDate today = LocalDate.now();
        LocalDate maxTargetDate = today.plusDays(3);

        return billRepository.findByUserEmailAndIsPaidFalseAndFinalPaymentDateBetweenOrderByFinalPaymentDateAsc(email, today, maxTargetDate);
    }

    public BillResponse payBill(Long billId)
    {
        return billRepository.findById(billId).map(bill ->
                {
                    bill.setPaid(true);
                    billRepository.save(bill);
                    return new BillResponse(true, "Fatura başarıyla ödendi.");
                })
                .orElse(new BillResponse(false, "Fatura bulunamadı."));
    }
}
