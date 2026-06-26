package com.jobportal.backend.repository;

import com.jobportal.backend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Get all applications by a candidate
    List<Application> findByCandidateId(Long candidateId);

    // Get all applications for a job
    List<Application> findByJobId(Long jobId);

    // Check if candidate already applied to a job
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    // Count applicants for a job
    long countByJobId(Long jobId);

    // Analytics
    long countByStatus(String status);

    long countByJobIdAndStatus(Long jobId, String status);
}