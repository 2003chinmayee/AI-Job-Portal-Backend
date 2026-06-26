package com.jobportal.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.backend.model.Application;
import com.jobportal.backend.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class AiRankingService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ─── Main method called by ApplicationService ─────────────────────────────
    public ResumeMatchAnalysis analyzeResumeForJob(String resumeText, Job job) {
        try {
            String prompt = buildPrompt(resumeText, job);
            String geminiResponse = callGemini(prompt);
            return parseGeminiResponse(geminiResponse, resumeText);
        } catch (Exception e) {
            System.out.println("⚠️ Gemini analysis failed: " + e.getMessage());
            return fallbackAnalysis(resumeText, job);
        }
    }

    // ─── Also used by ApplicationService.applyWithResume ─────────────────────
    public ResumeMatchAnalysis analyzeResumeForJob(String resumeText, Job job, Application application) {
        // Enrich resume text with form data the candidate filled in
        String enrichedText = enrichWithFormData(resumeText, application);
        return analyzeResumeForJob(enrichedText, job);
    }

    // ─── Build a detailed prompt for Gemini ──────────────────────────────────
    private String buildPrompt(String resumeText, Job job) {
        String safeResume = (resumeText == null || resumeText.isBlank())
                ? "No resume text available."
                : resumeText.substring(0, Math.min(resumeText.length(), 3000));

        return """
            You are an expert technical recruiter AI. Analyze the candidate's resume and profile
            against the job requirements and provide a detailed evaluation.

            === JOB DETAILS ===
            Title: %s
            Company: %s
            Description: %s
            Requirements/Skills Required: %s
            Experience Required: %s
            Job Type: %s

            === CANDIDATE RESUME / PROFILE ===
            %s

            === YOUR TASK ===
            Analyze the candidate's fit for this job. Be GENUINE and ACCURATE.
            Give a realistic score based on actual skill matches.

            Respond ONLY in this exact JSON format (no markdown, no extra text):
            {
              "score": <number between 0 and 100>,
              "skillsMatch": "<comma-separated matched skills from requirements>",
              "missingSkills": "<comma-separated missing required skills>",
              "strengths": "<2-3 sentences about candidate strengths>",
              "recommendation": "<one clear sentence: Highly Recommended / Good Match / Possible Match / Low Match>",
              "experienceMatch": "<one sentence about experience fit>",
              "educationMatch": "<one sentence about education fit>",
              "feedback": "Score breakdown: skills X/65, keywords Y/20, experience Z/10, education W/5."
            }
            """.formatted(
                nullSafe(job.getTitle()),
                nullSafe(job.getCompany()),
                nullSafe(job.getDescription()),
                nullSafe(job.getRequirements()),
                nullSafe(job.getExperience()),
                nullSafe(job.getJobType()),
                safeResume
        );
    }

    // ─── Call Gemini API ──────────────────────────────────────────────────────
    private String callGemini(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                },
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 1024
                )
        );

        String response = webClient.post()
                .uri("/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractTextFromGeminiResponse(response);
    }

    // ─── Extract text content from Gemini's response wrapper ─────────────────
    private String extractTextFromGeminiResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            return root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini response: " + e.getMessage());
        }
    }

    // ─── Parse Gemini's JSON output into ResumeMatchAnalysis ─────────────────
    private ResumeMatchAnalysis parseGeminiResponse(String geminiText, String resumeText) {
        try {
            // Strip markdown fences if present
            String cleaned = geminiText
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            JsonNode json = objectMapper.readTree(cleaned);

            double score = json.path("score").asDouble(0.0);
            // Clamp between 0-100
            score = Math.max(0.0, Math.min(100.0, score));

            return new ResumeMatchAnalysis(
                    Math.round(score * 10.0) / 10.0,
                    json.path("skillsMatch").asText("None"),
                    json.path("missingSkills").asText("None"),
                    json.path("strengths").asText("Not evaluated."),
                    json.path("recommendation").asText("Review manually."),
                    json.path("experienceMatch").asText("Not evaluated."),
                    json.path("educationMatch").asText("Not evaluated."),
                    json.path("feedback").asText("AI analysis complete.")
            );

        } catch (Exception e) {
            System.out.println("⚠️ Failed to parse Gemini JSON: " + e.getMessage());
            System.out.println("Raw response: " + geminiText);
            // Return a mid-range score with error note
            return new ResumeMatchAnalysis(
                    30.0,
                    "Could not parse",
                    "Could not parse",
                    "AI response could not be parsed. Please re-run analysis.",
                    "Manual review recommended.",
                    "Not evaluated.",
                    "Not evaluated.",
                    "Parse error — re-run AI analysis."
            );
        }
    }

    // ─── Enrich resume text with candidate's form data ────────────────────────
    // This ensures even if PDF extraction fails, Gemini still gets useful info
    private String enrichWithFormData(String resumeText, Application app) {
        if (app == null) return resumeText;

        StringBuilder enriched = new StringBuilder();

        // Add form-filled data first (always available)
        enriched.append("=== CANDIDATE PROFILE ===\n");
        if (app.getCandidateName() != null) enriched.append("Name: ").append(app.getCandidateName()).append("\n");
        if (app.getEducation()     != null) enriched.append("Education: ").append(app.getEducation()).append("\n");
        if (app.getCollegeName()   != null) enriched.append("College: ").append(app.getCollegeName()).append("\n");
        if (app.getUniversityName()!= null) enriched.append("University: ").append(app.getUniversityName()).append("\n");
        if (app.getPercentage()    != null) enriched.append("Percentage/CGPA: ").append(app.getPercentage()).append("\n");
        if (app.getYearOfPassing() != null) enriched.append("Year of Passing: ").append(app.getYearOfPassing()).append("\n");
        if (app.getCoverLetter()   != null) enriched.append("Cover Letter: ").append(app.getCoverLetter()).append("\n");

        // Add resume text if available
        if (resumeText != null && !resumeText.isBlank()) {
            enriched.append("\n=== RESUME TEXT ===\n").append(resumeText);
        } else {
            enriched.append("\n(Resume PDF could not be read — evaluate based on profile above)");
        }

        return enriched.toString();
    }

    // ─── Fallback if Gemini API fails completely ──────────────────────────────
    private ResumeMatchAnalysis fallbackAnalysis(String resumeText, Job job) {
        boolean hasResume = resumeText != null && !resumeText.isBlank();
        return new ResumeMatchAnalysis(
                hasResume ? 25.0 : 5.0,
                "Analysis pending",
                "Analysis pending",
                "Automated AI analysis temporarily unavailable. Manual review recommended.",
                "Please re-run AI analysis.",
                "Not evaluated.",
                "Not evaluated.",
                "Gemini API unavailable — re-run analysis to get accurate scores."
        );
    }

    private String nullSafe(String value) {
        return value == null ? "Not specified" : value;
    }

    // ─── Result record ────────────────────────────────────────────────────────
    public record ResumeMatchAnalysis(
            Double score,
            String skillsMatch,
            String missingSkills,
            String strengths,
            String recommendation,
            String experienceMatch,
            String educationMatch,
            String feedback
    ) {}
}
