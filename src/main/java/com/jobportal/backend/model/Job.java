
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
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;           // "Software Engineer"

    @Column(nullable = false)
    private String company;         // "TCS"

    @Column(nullable = false)
    private String location;        // "Pune"

    @Column(length = 2000)
    private String description;     // Job details

    private String requirements;    // "Java, Spring Boot"

    private String salary;          // "5-8 LPA"

    private String jobType;         // "FULL_TIME" / "PART_TIME"

    private String experience;      // "0-2 years"

    @Column(nullable = false)
    private String postedBy;        // Email of recruiter who posted

    @Column(nullable = false)
    private LocalDateTime postedAt; // When job was posted

    private boolean active = true;  // Is job still open?

    @PrePersist  // Runs automatically before saving to database
    public void prePersist() {
        postedAt = LocalDateTime.now();
    }
}