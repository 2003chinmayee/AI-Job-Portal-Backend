package com.jobportal.backend.controller;

import com.jobportal.backend.model.Job;
import com.jobportal.backend.service.JobService;
import com.jobportal.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationRepository applicationRepository;

    // POST /api/jobs — Create new job
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        try {
            return ResponseEntity.ok(jobService.createJob(job));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/jobs — All active jobs (candidate side)
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.getJobById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/jobs/search
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {
        if (keyword != null)  return ResponseEntity.ok(jobService.searchJobs(keyword));
        if (location != null) return ResponseEntity.ok(jobService.searchByLocation(location));
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    // GET /api/jobs/recruiter?email= — ALL jobs by recruiter
    @GetMapping("/recruiter")
    public ResponseEntity<List<Job>> getJobsByRecruiter(@RequestParam String email) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(email));
    }

    // ── GET /api/jobs/recruiter/active?email= ─────────────────────────────────
    // Only open + not expired + not fully hired → shown in My Jobs page
    @GetMapping("/recruiter/active")
    public ResponseEntity<List<Job>> getActiveJobsByRecruiter(@RequestParam String email) {
        LocalDate today = LocalDate.now();

        List<Job> active = jobService.getJobsByRecruiter(email)
                .stream()
                .filter(job -> {
                    // Must be active (not manually closed)
                    if (!job.isActive()) return false;

                    // Must not be expired
                    if (job.getClosingDate() != null && job.getClosingDate().isBefore(today))
                        return false;

                    // Must not be fully hired
                    long hired = applicationRepository.countByJobIdAndStatus(job.getId(), "HIRED");
                    if (job.getVacancies() != null && job.getVacancies() > 0
                            && hired >= job.getVacancies()) return false;

                    return true;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(active);
    }

    // ── GET /api/jobs/recruiter/history?email= ────────────────────────────────
    // Expired + manually closed + fully hired → shown in History page
    @GetMapping("/recruiter/history")
    public ResponseEntity<List<Map<String, Object>>> getHistoryJobsByRecruiter(
            @RequestParam String email) {

        LocalDate today = LocalDate.now();

        List<Map<String, Object>> history = jobService.getJobsByRecruiter(email)
                .stream()
                .filter(job -> {
                    // Manually closed
                    if (!job.isActive()) return true;
                    // Expired
                    if (job.getClosingDate() != null && job.getClosingDate().isBefore(today))
                        return true;
                    // Fully hired
                    long hired = applicationRepository.countByJobIdAndStatus(job.getId(), "HIRED");
                    return job.getVacancies() != null && job.getVacancies() > 0
                            && hired >= job.getVacancies();
                })
                .map(job -> {
                    long hired       = applicationRepository.countByJobIdAndStatus(job.getId(), "HIRED");
                    long applicants  = applicationRepository.countByJobId(job.getId());
                    long shortlisted = applicationRepository.countByJobIdAndStatus(job.getId(), "SHORTLISTED");

                    String status;
                    if (job.getVacancies() != null && job.getVacancies() > 0
                            && hired >= job.getVacancies()) {
                        status = "Completed";
                    } else if (job.getClosingDate() != null
                            && job.getClosingDate().isBefore(today)) {
                        status = "Expired";
                    } else {
                        status = "Closed";
                    }

                    return Map.<String, Object>of(
                            "jobId",       job.getId(),
                            "jobTitle",    job.getTitle() != null ? job.getTitle() : "",
                            "company",     job.getCompany() != null ? job.getCompany() : "",
                            "postedDate",  job.getPostedAt() != null ? job.getPostedAt().toString() : "",
                            "closingDate", job.getClosingDate() != null ? job.getClosingDate().toString() : "",
                            "vacancies",   job.getVacancies() != null ? job.getVacancies() : 0,
                            "hired",       hired,
                            "applicants",  applicants,
                            "shortlisted", shortlisted,
                            "status",      status
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    // PUT /api/jobs/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job job) {
        try {
            return ResponseEntity.ok(jobService.updateJob(id, job));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/jobs/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok("Job deleted successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/jobs/{id}/toggle
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleJob(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.toggleJobStatus(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/jobs/{id}/count
    @GetMapping("/{id}/count")
    public ResponseEntity<Long> getApplicantCount(@PathVariable Long id) {
        return ResponseEntity.ok(applicationRepository.countByJobId(id));
    }
}
