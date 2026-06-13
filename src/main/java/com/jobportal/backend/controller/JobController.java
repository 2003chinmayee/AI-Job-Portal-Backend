package com.jobportal.backend.controller;

import com.jobportal.backend.model.Job;
import com.jobportal.backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobService jobService;

    // POST /api/jobs — Create new job
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        try {
            Job savedJob = jobService.createJob(job);
            return ResponseEntity.ok(savedJob);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/jobs — Get all active jobs
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/1 — Get single job
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.getJobById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/jobs/search?keyword=java — Search jobs
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {

        if (keyword != null) {
            return ResponseEntity.ok(jobService.searchJobs(keyword));
        }
        if (location != null) {
            return ResponseEntity.ok(jobService.searchByLocation(location));
        }
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/recruiter?email=test@gmail.com
    @GetMapping("/recruiter")
    public ResponseEntity<List<Job>> getJobsByRecruiter(
            @RequestParam String email) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(email));
    }

    // PUT /api/jobs/1 — Update job
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(
            @PathVariable Long id,
            @RequestBody Job job) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, job));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/jobs/1 — Delete job
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok("Job deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}