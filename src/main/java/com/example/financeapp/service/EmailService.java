package com.example.financeapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender mailSender;

    public void sendOtp(String email,String code)
    {
        try
        {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("OTP Kodu");
            message.setText("Kodunuz: " + code);

            mailSender.send(message);

            System.out.println("MAIL GONDERILDI");
        }
        catch (Exception e)
        {
            System.out.println("MAIL ERROR");
            e.printStackTrace();
        }
    }
}
