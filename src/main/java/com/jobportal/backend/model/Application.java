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
    private Long candidateId;

    @Column(nullable = false)
    private Long jobId;

    private String candidateName;
    private String candidateEmail;
    private String phone;

    private String education;

    private String collegeName;

    private String universityName;

    private String percentage;

    private String gender;

    private String dateOfBirth;

    private String yearOfPassing;

    private String resumeFileName;

    private String jobTitle;
    private String company;

    /*
     * AI Resume Ranking Score
     * Example: 85.5
     */
    private Double aiScore;

    /*
     * AI Feedback
     * Example:
     * Strong Java skills.
     * Missing Docker.
     * Missing AWS.
     */
    @Column(length = 3000)
    private String aiFeedback;

    @Column(nullable = false)
    private String status;       // APPLIED, SHORTLISTED, REJECTED, HIRED

    private String coverLetter;

    // ─── Interview Details ────────────────────────────────────────────────────

    private String interviewDate;

    private String interviewTime;

    private String interviewMode;   // Zoom / Google Meet / In-Person

    private String meetingLink;

    private Integer interviewRounds;

    private String contactPerson;   // 🆕 NEW — HR name shown in shortlist email

    private String contactEmail;    // 🆕 NEW — HR email shown in shortlist email

    // ─── AI Ranking Details ───────────────────────────────────────────────────

    /*
     * 🆕 NEW — Comma-separated matched skills
     * Example: "Java, Spring Boot, MySQL"
     */
    @Column(length = 1000)
    private String skillsMatch;

    /*
     * 🆕 NEW — Comma-separated missing skills
     * Example: "Docker, Kubernetes, AWS"
     */
    @Column(length = 1000)
    private String missingSkills;

    /*
     * 🆕 NEW — AI-written strengths paragraph
     * Example: "Strong backend experience with 3 years in Java..."
     */
    @Column(length = 2000)
    private String strengths;

    /*
     * 🆕 NEW — AI's final recommendation
     * Example: "Highly recommended. Matches 87% of requirements."
     */
    @Column(length = 1000)
    private String aiRecommendation;

    /*
     * 🆕 NEW — Does experience match?
     * Example: "3 years / Required: 2+ years ✓"
     */
    private String experienceMatch;

    /*
     * 🆕 NEW — Does education match?
     * Example: "B.Tech Computer Science ✓"
     */
    private String educationMatch;

    // ─── Hiring Details ───────────────────────────────────────────────────────

    private String joiningDate;

    private String officeLocation;

    /*
     * 🆕 NEW — Offered salary shown in hire email
     * Example: "12 LPA"
     */
    private String salary;

    // ─── Metadata ─────────────────────────────────────────────────────────────

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    public void prePersist() {
        appliedAt = LocalDateTime.now();
        status = "APPLIED";
    }
}