package com.jobportal.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    public String analyzeResume(String resumeText) {

        String prompt = "You are an ATS Resume Analyzer. " +
                "Analyze this resume and respond ONLY with valid JSON (no markdown, no extra text) " +
                "in EXACTLY this format:\n" +
                "{\n" +
                "  \"score\": 78,\n" +
                "  \"strongSkills\": [\"Java\", \"Spring Boot\", \"React\"],\n" +
                "  \"missingSkills\": [\"Docker\", \"AWS\", \"JUnit\"],\n" +
                "  \"recommendations\": [\"Add quantified achievements\", \"Add cloud experience\"],\n" +
                "  \"summary\": \"A short 2-3 sentence summary of the resume quality.\"\n" +
                "}\n\n" +
                "Resume Text:\n" + resumeText;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {

            String response = webClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {
            e.printStackTrace();
            return "AI Error: " + e.getMessage();
        }
    }
}