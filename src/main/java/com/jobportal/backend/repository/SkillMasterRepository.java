package com.jobportal.backend.repository;

import com.jobportal.backend.model.SkillMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillMasterRepository
        extends JpaRepository<SkillMaster, Long> {

    boolean existsBySkillNameIgnoreCase(String skillName);

}