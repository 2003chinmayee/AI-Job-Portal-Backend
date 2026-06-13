package com.jobportal.backend.service;

import com.jobportal.backend.model.Application;
import com.jobportal.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    // Apply for a job
    public Application applyForJob(Application application) {

        // Check if already applied
        if (applicationRepository.existsByCandidateIdAndJobId(
                application.getCandidateId(),
                application.getJobId())) {
            throw new RuntimeException("You already applied for this job!");
        }

        return applicationRepository.save(application);
    }

    // Get all applications by candidate
    public List<Application> getMyApplications(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    // Get all applications for a job (recruiter view)
    public List<Application> getJobApplications(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // Update application status (recruiter action)
    public Application updateStatus(Long applicationId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found!"));
        application.setStatus(status);
        return applicationRepository.save(application);
    }
}