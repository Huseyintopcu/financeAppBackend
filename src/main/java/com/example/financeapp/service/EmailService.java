package com.example.financeapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String email,String code)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Kodu");
        message.setText("Kodunuz: "+ code);

        mailSender.send(message);
    }
}
