package com.jobportal.backend.controller;

import com.jobportal.backend.ai.GeminiService;
import com.jobportal.backend.ai.PdfUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/hello")
    public String hello() {
        System.out.println("HELLO HIT");
        return "HELLO WORKING";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestBody String resumeText) {
        return geminiService.analyzeResume(resumeText);
    }

    @PostMapping("/analyze-pdf")
    public String analyzePdf(@RequestParam("file") MultipartFile file) {

        try {

            File tempFile = File.createTempFile("resume", ".pdf");

            file.transferTo(tempFile);

            String resumeText = PdfUtil.extractText(tempFile);

            return geminiService.analyzeResume(resumeText);

        } catch (Exception e) {

            e.printStackTrace();

            return "Error reading PDF: " + e.getMessage();
        }
    }
}