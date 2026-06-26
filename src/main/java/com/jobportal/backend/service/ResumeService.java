package com.jobportal.backend.service;

import com.jobportal.backend.model.Resume;
import com.jobportal.backend.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public Resume saveResume(Resume resume) {
        return resumeRepository.save(resume);
    }

    public Optional<Resume> getLatestResumeByUserId(Long userId) {
        return resumeRepository.findTopByUserIdOrderByIdDesc(userId);
    }
}
