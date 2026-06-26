package com.jobportal.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;   // CANDIDATE or RECRUITER

    private String phone;

    private String location;

    // ✅ NEW - added for My Profile feature
    private String bio;

    // ✅ NEW - added for My Profile feature (e.g. "Java, React, Spring Boot")
    private String skills;

    private String education;

    @Column(length = 2000)
    private String experience;

    private String resumeUrl;

    public String test() {
        return getEmail();
    }
}