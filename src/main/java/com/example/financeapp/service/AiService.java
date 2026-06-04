package com.example.financeapp.service;

import com.example.financeapp.dto.AiIBillResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    public AiIBillResponse analyzeBillWithAi(MultipartFile file)
    {
        try
        {

            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";


            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text",
                    "Analiz etmeni istediğim fatura/fiş ekteki görseldedir. " +
                            "Faturadaki her bir ürünü TEK TEK çıkar. " +
                            "Her ürün için title, quantity, amount ve category alanlarını üret. " +

                            "YANIT KURALI ÇOK ÖNEMLİ: " +
                            "Sadece ve sadece JSON ARRAY döndür. Başka hiçbir metin yazma. " +
                            "Markdown kullanma (```json yasak). " +

                            "Format kesinlikle şu olmalı: " +
                            "[ " +
                            "{\"title\":\"string\",\"quantity\":int,\"amount\":double,\"category\":\"FOOD\"}, " +
                            "{\"title\":\"string\",\"quantity\":int,\"amount\":double,\"category\":\"FOOD\"} " +
                            "] " +

                            "Kategori sadece şu değerlerden biri olmalı: FOOD, SNACKS, TRANSPORT, HEALTH, BILLS, ENTERTAINMENT, SHOPPING, EDUCATION, OTHER."
            );


            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mime_type", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inline_data", inlineData);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Arrays.asList(textPart, imagePart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");

            Map<String, Object> candidate = candidates.get(0);

            Map<String, Object> contentResp = (Map<String, Object>) candidate.get("content");

            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResp.get("parts");

            String rawJsonResult = (String) parts.get(0).get("text");

            String cleanedJson = rawJsonResult
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            List<AiIBillResponse.BillItem> items = objectMapper.readValue(cleanedJson, new TypeReference<List<AiIBillResponse.BillItem>>() {});

            AiIBillResponse responseDto = new AiIBillResponse();
            responseDto.setItems(items);

            return responseDto;

        }
        catch (Exception e)
        {
            throw new RuntimeException("Yapay zeka analiz işlemi başarısız oldu: " + e.getMessage());
        }
    }
}