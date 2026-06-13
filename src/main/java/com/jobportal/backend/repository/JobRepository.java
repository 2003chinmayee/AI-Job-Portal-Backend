
package com.jobportal.backend.repository;

import com.jobportal.backend.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Get all jobs by a specific recruiter
    // Becomes: SELECT * FROM jobs WHERE posted_by = ?
    List<Job> findByPostedBy(String email);

    // Search jobs by title containing a keyword
    // Becomes: SELECT * FROM jobs WHERE title LIKE '%keyword%'
    List<Job> findByTitleContainingIgnoreCase(String keyword);

    // Get all active jobs
    // Becomes: SELECT * FROM jobs WHERE active = true
    List<Job> findByActiveTrue();

    // Search by location
    List<Job> findByLocationContainingIgnoreCase(String location);

    // Search by company
    List<Job> findByCompanyContainingIgnoreCase(String company);
}