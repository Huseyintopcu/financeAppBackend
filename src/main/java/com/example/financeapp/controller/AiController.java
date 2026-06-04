package com.example.financeapp.controller;

import com.example.financeapp.dto.AiIBillResponse;
import com.example.financeapp.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController
{
    private final AiService aiService;

    @PostMapping("/process")
    public ResponseEntity<AiIBillResponse> processBill(@RequestParam("file") MultipartFile file)
    {
        AiIBillResponse response = aiService.analyzeBillWithAi(file);
        return ResponseEntity.ok(response);
    }

}
