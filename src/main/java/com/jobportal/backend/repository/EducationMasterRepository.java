package com.jobportal.backend.repository;

import com.jobportal.backend.model.EducationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EducationMasterRepository
        extends JpaRepository<EducationMaster, Long> {

    boolean existsByEducationNameIgnoreCase(String educationName);

}