package com.example.financeapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService
{
    @Value("${resend.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String email, String code)
    {
        String url = "https://resend.com";

        try
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);


            Map<String, Object> body = new HashMap<>();
            body.put("from", "otp@moneta-app.cloud-ip.cc");
            body.put("to", email);
            body.put("subject", "OTP Kodu");
            body.put("html", "<strong>Kodunuz: " + code + "</strong>");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);


            restTemplate.postForEntity(url, request, String.class);

            System.out.println("MAIL GONDERILDI");
        } catch (Exception e) {
            System.out.println("MAIL ERROR");
            e.printStackTrace();
        }
    }
}
