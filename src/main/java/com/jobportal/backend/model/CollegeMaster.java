package com.jobportal.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "college_master")
public class CollegeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String collegeName;

    public CollegeMaster() {
    }

    public CollegeMaster(String collegeName) {
        this.collegeName = collegeName;
    }

    public Long getId() {
        return id;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }
}