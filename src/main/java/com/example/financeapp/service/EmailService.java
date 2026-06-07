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

    private RestTemplate restTemplate;

    public void sendOtp(String email, String code)
    {
        String url = "https://resend.com";

        this.restTemplate = new RestTemplate();

        try {
            System.out.println("LOG: Mail gonderimi basladi. Hedef: " + email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey.trim());

            Map<String, Object> body = new HashMap<>();

            body.put("from", "Moneta App <otp@moneta-app.cloud-ip.cc>");
            body.put("to", email.trim());
            body.put("subject", "OTP Kodu");
            body.put("html", "<strong>Kodunuz: " + code + "</strong>");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String response = restTemplate.postForObject(url, request, String.class);

            System.out.println("LOG: RESEND SUNUCU YANITI -> " + response);
            System.out.println("MAIL GONDERILDI");

        }
        catch (org.springframework.web.client.HttpStatusCodeException e)
        {
            System.out.println("LOG HATA (API): " + e.getRawStatusCode() + " - " + e.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            System.out.println("LOG HATA (SISTEM): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
