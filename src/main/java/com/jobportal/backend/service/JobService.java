package com.jobportal.backend.service;

import com.jobportal.backend.model.Job;
import com.jobportal.backend.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    // POST a new job
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    // GET all active jobs
    public List<Job> getAllJobs() {
        return jobRepository.findByActiveTrue();
    }

    // GET single job by id
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found!"));
    }

    // GET jobs posted by a recruiter
    public List<Job> getJobsByRecruiter(String email) {
        return jobRepository.findByPostedBy(email);
    }

    // SEARCH jobs by keyword
    public List<Job> searchJobs(String keyword) {
        return jobRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // SEARCH by location
    public List<Job> searchByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }

    // DELETE job
    public void deleteJob(Long id) {
        Job job = getJobById(id);
        job.setActive(false); // Soft delete — just mark inactive!
        jobRepository.save(job);
    }

    // UPDATE job
    public Job updateJob(Long id, Job updatedJob) {
        Job existing = getJobById(id);
        existing.setTitle(updatedJob.getTitle());
        existing.setCompany(updatedJob.getCompany());
        existing.setLocation(updatedJob.getLocation());
        existing.setDescription(updatedJob.getDescription());
        existing.setRequirements(updatedJob.getRequirements());
        existing.setSalary(updatedJob.getSalary());
        existing.setJobType(updatedJob.getJobType());
        existing.setExperience(updatedJob.getExperience());
        return jobRepository.save(existing);
    }
}