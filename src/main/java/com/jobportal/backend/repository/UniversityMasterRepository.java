package com.jobportal.backend.repository;
import org.springframework.stereotype.Repository;

import com.jobportal.backend.model.UniversityMaster;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UniversityMasterRepository
        extends JpaRepository<UniversityMaster, Long> {

    boolean existsByUniversityNameIgnoreCase(String universityName);
}