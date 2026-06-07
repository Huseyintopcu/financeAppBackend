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


    @Value("${RESEND_API_KEY:${resend.api.key:}}")
    private String apiKey;

    RestTemplate restTemplate = new RestTemplate();

    public void sendOtp(String email, String code)
    {
        String url = "https://resend.com";

        this.restTemplate = new RestTemplate();

        try {
            System.out.println("--- GONDERIM BASLADI ---");
            System.out.println("Hedef E-posta: " + email);
            System.out.println("API Key Uzunlugu: " + (apiKey != null ? apiKey.length() : "NULL"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim()); // Tokenı manuel ve kesin ekliyoruz

            Map<String, Object> body = new HashMap<>();
            body.put("from", "Moneta App <otp@moneta-app.cloud-ip.cc>"); // Doğruladığımız alan adı
            body.put("to", email.trim());
            body.put("subject", "OTP Kodu");
            body.put("html", "<strong>Kodunuz: " + code + "</strong>");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            System.out.println("Resend API sunucularina HTTP POST firlatiliyor...");


            String response = restTemplate.postForObject(url, request, String.class);

            System.out.println("RESEND SUNUCU YANITI: " + response);
            System.out.println("--- MAILLER RESEND'E TESLIM EDILDI ---");

        }
        catch (org.springframework.web.client.HttpStatusCodeException e)
        {
            System.out.println("RESEND API HTTP HATASI: " + e.getRawStatusCode() + " - " + e.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            System.out.println("SISTEM BAGLANTI HATASI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
