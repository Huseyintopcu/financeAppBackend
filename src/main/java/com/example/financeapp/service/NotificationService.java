package com.example.financeapp.service;

import com.example.financeapp.entity.Bill;
import com.example.financeapp.repository.BillRepository;
import com.example.financeapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService
{
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 30 9 * * *")
    public void checkUpcomingBills()
    {
        LocalDate today = LocalDate.now();
        LocalDate maxTargetDate = today.plusDays(3);

        List<Bill> upcomingBills = billRepository.findByIsPaidFalseAndFinalPaymentDateBetween(today, maxTargetDate);

        for (Bill bill : upcomingBills)
        {
            String userEmail = bill.getUserEmail();
            int dayLeft = (bill.getFinalPaymentDate().getDayOfMonth())-(today.getDayOfMonth());


            userRepository.findByEmail(userEmail).ifPresent(user ->
            {
               String deviceToken = user.getFcmToken();

                if (deviceToken != null && !deviceToken.trim().isEmpty()) {
                    String title = "🚨 Fatura Hatırlatıcı";
                    String body = bill.getTitle() + " faturanızın son ödeme tarihine "+ dayLeft +" gün kaldı! Tutar: ₺" + bill.getAmount();

                    fcmService.sendPushNotification(deviceToken, title, body);
                }
            });
        }
    }


}
