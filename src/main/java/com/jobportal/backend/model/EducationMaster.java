package com.jobportal.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "education_master")
public class EducationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String educationName;

    public EducationMaster() {
    }

    public EducationMaster(String educationName) {
        this.educationName = educationName;
    }

    public Long getId() {
        return id;
    }

    public String getEducationName() {
        return educationName;
    }

    public void setEducationName(String educationName) {
        this.educationName = educationName;
    }
}