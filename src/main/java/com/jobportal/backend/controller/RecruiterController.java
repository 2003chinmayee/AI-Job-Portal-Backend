package com.jobportal.backend.controller;

import com.jobportal.backend.model.Application;
import com.jobportal.backend.service.ApplicationService;
import com.jobportal.backend.service.EmailService;
import com.jobportal.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
@CrossOrigin(origins = "*")
public class RecruiterController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    // ── AI analyze resumes ────────────────────────────────────────────────────
    @PostMapping("/jobs/{jobId}/analyze-resumes")
    public ResponseEntity<?> analyzeResumes(@PathVariable Long jobId) {
        try {
            List<Application> ranked = applicationService.analyzeAndRankApplications(jobId);
            return ResponseEntity.ok(Map.of(
                    "message", "AI resume analysis completed.",
                    "totalApplicants", ranked.size(),
                    "applications", ranked
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Get ranked applications ───────────────────────────────────────────────
    @GetMapping("/jobs/{jobId}/ranked-applications")
    public ResponseEntity<?> getRanked(@PathVariable Long jobId) {
        try {
            return ResponseEntity.ok(applicationService.getRankedApplications(jobId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── SHORTLIST ─────────────────────────────────────────────────────────────
    @PutMapping("/shortlist/{applicationId}")
    public ResponseEntity<?> shortlist(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> details) {
        try {
            Application app = applicationService.getApplicationById(applicationId);
            app.setStatus("SHORTLISTED");
            app.setInterviewDate(details.get("interviewDate"));
            app.setInterviewTime(details.get("interviewTime"));
            app.setInterviewMode(details.get("interviewMode"));
            app.setMeetingLink(details.get("meetingLink"));
            app.setContactPerson(details.get("contactPerson"));
            app.setContactEmail(details.get("contactEmail"));
            app.setSalary(details.get("salary"));
            app.setOfficeLocation(details.get("officeLocation"));
            applicationService.saveApplication(app);

            // ── Notification with full debug ──────────────────────────────────
            try {
                System.out.println("🔔 Attempting notification for: " + app.getCandidateEmail());
                System.out.println("🔔 Job title: " + app.getJobTitle());
                notificationService.createStatusUpdateNotification(
                        app.getCandidateEmail(),
                        app.getJobTitle(),
                        "SHORTLISTED — Interview on " +
                                (app.getInterviewDate() != null ? app.getInterviewDate() : "TBD")
                );
                System.out.println("✅ Notification saved successfully!");
            } catch (Exception e) {
                System.out.println("❌ Notification FAILED: " + e.getMessage());
                e.printStackTrace();
            }

            // ── Email ─────────────────────────────────────────────────────────
            try {
                emailService.sendShortlistEmail(app);
                System.out.println("✅ Email sent to: " + app.getCandidateEmail());
            } catch (Exception e) {
                System.out.println("⚠️ Email failed: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Candidate shortlisted!",
                    "candidateName", app.getCandidateName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── REJECT ────────────────────────────────────────────────────────────────
    @PutMapping("/reject/{applicationId}")
    public ResponseEntity<?> reject(@PathVariable Long applicationId) {
        try {
            Application app = applicationService.getApplicationById(applicationId);
            app.setStatus("REJECTED");
            applicationService.saveApplication(app);

            // ── Notification with full debug ──────────────────────────────────
            try {
                System.out.println("🔔 Attempting notification for: " + app.getCandidateEmail());
                notificationService.createStatusUpdateNotification(
                        app.getCandidateEmail(),
                        app.getJobTitle(),
                        "REJECTED — Thank you for applying to " + app.getCompany()
                );
                System.out.println("✅ Notification saved successfully!");
            } catch (Exception e) {
                System.out.println("❌ Notification FAILED: " + e.getMessage());
                e.printStackTrace();
            }

            // ── Email ─────────────────────────────────────────────────────────
            try {
                emailService.sendRejectionEmail(app);
            } catch (Exception e) {
                System.out.println("⚠️ Email failed: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Candidate rejected!",
                    "candidateName", app.getCandidateName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // ── HIRE ──────────────────────────────────────────────────────────────────
    @PutMapping("/hire/{applicationId}")
    public ResponseEntity<?> hire(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> details) {
        try {
            Application app = applicationService.getApplicationById(applicationId);
            app.setStatus("HIRED");
            app.setJoiningDate(details.get("joiningDate"));
            app.setSalary(details.get("salary"));
            app.setOfficeLocation(details.get("officeLocation"));
            app.setContactPerson(details.get("contactPerson"));
            app.setContactEmail(details.get("contactEmail"));
            applicationService.saveApplication(app);

            // ── Notification with full debug ──────────────────────────────────
            try {
                System.out.println("🔔 Attempting notification for: " + app.getCandidateEmail());
                notificationService.createStatusUpdateNotification(
                        app.getCandidateEmail(),
                        app.getJobTitle(),
                        "HIRED 🎊 — Welcome to " + app.getCompany() +
                                "! Joining: " + (app.getJoiningDate() != null ? app.getJoiningDate() : "TBD")
                );
                System.out.println("✅ Notification saved successfully!");
            } catch (Exception e) {
                System.out.println("❌ Notification FAILED: " + e.getMessage());
                e.printStackTrace();
            }

            // ── Email ─────────────────────────────────────────────────────────
            try {
                emailService.sendHireEmail(app);
            } catch (Exception e) {
                System.out.println("⚠️ Email failed: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Candidate hired!",
                    "candidateName", app.getCandidateName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
