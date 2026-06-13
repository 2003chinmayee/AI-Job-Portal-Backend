package com.jobportal.backend.controller;

import com.jobportal.backend.model.Application;
import com.jobportal.backend.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    // POST /api/applications — Apply for a job
    @PostMapping
    public ResponseEntity<?> applyForJob(@RequestBody Application application) {
        try {
            Application saved = applicationService.applyForJob(application);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/applications/candidate/1 — Get my applications
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<Application>> getMyApplications(
            @PathVariable Long candidateId) {
        return ResponseEntity.ok(
                applicationService.getMyApplications(candidateId));
    }

    // GET /api/applications/job/1 — Get all applicants for a job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getJobApplications(
            @PathVariable Long jobId) {
        return ResponseEntity.ok(
                applicationService.getJobApplications(jobId));
    }

    // PUT /api/applications/1/status — Update status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            Application updated = applicationService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}