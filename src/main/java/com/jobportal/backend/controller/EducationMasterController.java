package com.jobportal.backend.controller;

import com.jobportal.backend.model.EducationMaster;
import com.jobportal.backend.repository.EducationMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/education")
@CrossOrigin(origins = "http://localhost:3001")
public class EducationMasterController {

    @Autowired
    private EducationMasterRepository educationMasterRepository;

    @GetMapping
    public List<EducationMaster> getAllEducation() {
        return educationMasterRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addEducation(
            @RequestBody EducationMaster education) {

        boolean exists =
                educationMasterRepository
                        .existsByEducationNameIgnoreCase(
                                education.getEducationName());

        if (exists) {
            return ResponseEntity
                    .badRequest()
                    .body("Education already exists");
        }

        return ResponseEntity.ok(
                educationMasterRepository.save(education)
        );
    }
}