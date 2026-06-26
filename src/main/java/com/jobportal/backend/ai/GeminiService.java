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

    // ---------- COMMON HELPER: Calls Gemini and returns raw text ----------
    private String callGemini(String prompt) {

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

    // ---------- RESUME ANALYZER ----------
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

        return callGemini(prompt);
    }

    // ---------- STATUS EMAIL GENERATOR ----------
    public String generateStatusEmail(
            String candidateName,
            String jobTitle,
            String company,
            String salary,
            String jobDescription,
            String status,
            String interviewDate,
            String interviewTime,
            String interviewPlatform,
            String interviewLink
    ) {

        String prompt;

        if (status.equalsIgnoreCase("SHORTLISTED")) {

            prompt = "You are an HR recruiter writing a professional email to a job candidate. " +
                    "Respond ONLY with valid JSON (no markdown, no extra text) in EXACTLY this format:\n" +
                    "{\n" +
                    "  \"subject\": \"Email subject line\",\n" +
                    "  \"body\": \"Full email body text with proper greeting, paragraphs and signature\"\n" +
                    "}\n\n" +
                    "Write a warm, professional email informing the candidate they have been SHORTLISTED.\n" +
                    "Candidate Name: " + candidateName + "\n" +
                    "Job Title: " + jobTitle + "\n" +
                    "Company: " + company + "\n" +
                    "Salary Range: " + salary + "\n" +
                    "Job Responsibilities: " + jobDescription + "\n" +
                    "Interview Date: " + interviewDate + "\n" +
                    "Interview Time: " + interviewTime + "\n" +
                    "Interview Platform: " + interviewPlatform + "\n" +
                    "Interview Link: " + interviewLink + "\n\n" +
                    "Mention the role, salary range, key responsibilities, and clearly state the interview date, time, platform and link. " +
                    "Sign off as 'Recruitment Team, " + company + "'.";

        } else if (status.equalsIgnoreCase("HIRED")) {

            prompt = "You are an HR recruiter writing a professional offer email to a job candidate. " +
                    "Respond ONLY with valid JSON (no markdown, no extra text) in EXACTLY this format:\n" +
                    "{\n" +
                    "  \"subject\": \"Email subject line\",\n" +
                    "  \"body\": \"Full email body text with proper greeting, paragraphs and signature\"\n" +
                    "}\n\n" +
                    "Write a warm, congratulatory email informing the candidate they have been HIRED/SELECTED.\n" +
                    "Candidate Name: " + candidateName + "\n" +
                    "Job Title: " + jobTitle + "\n" +
                    "Company: " + company + "\n" +
                    "Salary: " + salary + "\n" +
                    "Job Responsibilities: " + jobDescription + "\n\n" +
                    "Mention the role, salary, key responsibilities, and tell them HR will reach out shortly with onboarding details. " +
                    "Sign off as 'Recruitment Team, " + company + "'.";

        } else { // REJECTED

            prompt = "You are an HR recruiter writing a professional but kind rejection email to a job candidate. " +
                    "Respond ONLY with valid JSON (no markdown, no extra text) in EXACTLY this format:\n" +
                    "{\n" +
                    "  \"subject\": \"Email subject line\",\n" +
                    "  \"body\": \"Full email body text with proper greeting, paragraphs and signature\"\n" +
                    "}\n\n" +
                    "Write a polite, encouraging rejection email for this application.\n" +
                    "Candidate Name: " + candidateName + "\n" +
                    "Job Title: " + jobTitle + "\n" +
                    "Company: " + company + "\n\n" +
                    "Keep it short, respectful, and encourage them to apply for future roles. " +
                    "Sign off as 'Recruitment Team, " + company + "'.";
        }

        return callGemini(prompt);
    }
}