package com.jobportal.backend.controller;

import com.jobportal.backend.ai.ResumeAnalyzerService;
import com.jobportal.backend.model.Resume;
import com.jobportal.backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin("*")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private ResumeAnalyzerService resumeAnalyzerService;

    @PostMapping
    public String uploadResume(@RequestBody Resume resume) {

        resumeService.saveResume(resume);

        return resumeAnalyzerService
                .analyzeResume(resume.getExtractedText());
    }
}