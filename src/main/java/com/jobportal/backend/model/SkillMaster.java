package com.jobportal.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skill_master")
public class SkillMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String skillName;

    public SkillMaster() {
    }

    public SkillMaster(String skillName) {
        this.skillName = skillName;
    }

    public Long getId() {
        return id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}