package com.jobportal.backend.controller;

import com.jobportal.backend.model.Application;
import com.jobportal.backend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // ─── Apply for a job ──────────────────────────────────────────────────────
    // ─── Apply for a job (multipart with resume PDF) ──────────────────────────
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(
            @RequestParam("resumeFile")     MultipartFile resumeFile,
            @RequestParam("candidateId")    Long candidateId,
            @RequestParam("jobId")          Long jobId,
            @RequestParam("candidateName")  String candidateName,
            @RequestParam("candidateEmail") String candidateEmail,
            @RequestParam("phone")          String phone,
            @RequestParam("education")      String education,
            @RequestParam("collegeName")    String collegeName,
            @RequestParam("universityName") String universityName,
            @RequestParam("percentage")     String percentage,
            @RequestParam("gender")         String gender,
            @RequestParam("dateOfBirth")    String dateOfBirth,
            @RequestParam("yearOfPassing")  String yearOfPassing,
            @RequestParam("coverLetter")    String coverLetter,
            @RequestParam("skills")         String skills) {
        try {
            Application saved = applicationService.applyWithResume(
                    resumeFile, candidateId, jobId,
                    candidateName, candidateEmail,
                    phone, education, collegeName, universityName,
                    percentage, gender, dateOfBirth, yearOfPassing,
                    coverLetter, skills);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to apply: " + e.getMessage());
        }
    }

    // ─── Get ALL applications for a candidate (dashboard) ─────────────────────
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Application>> getCandidateApplications(@PathVariable Long candidateId) {
        List<Application> apps = applicationService.getMyApplications(candidateId);  // ✅ matches service
        return ResponseEntity.ok(apps);
    }

    // ─── ACTIVE: only APPLIED status (awaiting recruiter decision) ────────────
    @GetMapping("/candidate/{candidateId}/active")
    public ResponseEntity<List<Application>> getActiveApplications(@PathVariable Long candidateId) {
        List<Application> active = applicationService.getMyApplications(candidateId)
                .stream()
                .filter(app -> "APPLIED".equals(app.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(active);
    }

    // ─── HISTORY: SHORTLISTED / HIRED / REJECTED always show here ────────────
    //             APPLIED older than 30 days also archived here
    @GetMapping("/candidate/{candidateId}/history")
    public ResponseEntity<List<Application>> getHistoryApplications(@PathVariable Long candidateId) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);

        List<Application> history = applicationService.getMyApplications(candidateId)  // ✅ matches service
                .stream()
                .filter(app -> {
                    String status = app.getStatus();

                    // Recruiter took action → always in history
                    if ("SHORTLISTED".equals(status) ||
                            "HIRED".equals(status) ||
                            "REJECTED".equals(status)) {
                        return true;
                    }

                    // Still APPLIED but no response in 30 days → archive it
                    if ("APPLIED".equals(status) && app.getAppliedAt() != null) {
                        return app.getAppliedAt().isBefore(cutoff);
                    }

                    return false;
                })
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    // ─── SUMMARY counts for dashboard stats ───────────────────────────────────
    @GetMapping("/candidate/{candidateId}/summary")
    public ResponseEntity<Map<String, Object>> getApplicationSummary(@PathVariable Long candidateId) {
        List<Application> all = applicationService.getMyApplications(candidateId);  // ✅ matches service

        long appliedCount     = all.stream().filter(a -> "APPLIED".equals(a.getStatus())).count();
        long shortlistedCount = all.stream().filter(a -> "SHORTLISTED".equals(a.getStatus())).count();
        long hiredCount       = all.stream().filter(a -> "HIRED".equals(a.getStatus())).count();
        long rejectedCount    = all.stream().filter(a -> "REJECTED".equals(a.getStatus())).count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("total",       all.size());
        summary.put("applied",     appliedCount);
        summary.put("shortlisted", shortlistedCount);
        summary.put("hired",       hiredCount);
        summary.put("rejected",    rejectedCount);

        return ResponseEntity.ok(summary);
    }

    // ─── Get all applications for a job (recruiter view) ──────────────────────
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(@PathVariable Long jobId) {
        List<Application> apps = applicationService.getJobApplications(jobId);  // ✅ matches service
        return ResponseEntity.ok(apps);
    }

    // ─── Update application status (recruiter: shortlist / hire / reject) ──────
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String newStatus = body.get("status");
            Application updated = applicationService.updateStatus(id, newStatus);  // ✅ matches service
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update status: " + e.getMessage());
        }
    }

    // ─── Get single application by ID ─────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getApplicationById(@PathVariable Long id) {
        try {
            Application app = applicationService.getApplicationById(id);  // ✅ matches service
            return ResponseEntity.ok(app);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Application not found");
        }
    }
}
