package com.jobportal.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;    // Who applied

    @Column(nullable = false)
    private Long jobId;          // Which job

    private String candidateName;
    private String candidateEmail;
    private String jobTitle;
    private String company;

    @Column(nullable = false)
    private String status;       // APPLIED, SHORTLISTED, REJECTED, HIRED

    private String coverLetter;  // Optional message from candidate

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    public void prePersist() {
        appliedAt = LocalDateTime.now();
        status = "APPLIED";
    }
}