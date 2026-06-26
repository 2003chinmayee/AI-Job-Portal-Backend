package com.jobportal.backend.repository;

import com.jobportal.backend.model.CollegeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollegeMasterRepository
        extends JpaRepository<CollegeMaster, Long> {

    boolean existsByCollegeNameIgnoreCase(String collegeName);

}