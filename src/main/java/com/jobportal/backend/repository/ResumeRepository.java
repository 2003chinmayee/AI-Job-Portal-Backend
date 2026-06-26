package com.jobportal.backend.repository;

import com.jobportal.backend.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findTopByUserIdOrderByIdDesc(Long userId);
}
