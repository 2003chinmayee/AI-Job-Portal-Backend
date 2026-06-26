package com.jobportal.backend.controller;

import com.jobportal.backend.model.UniversityMaster;
import com.jobportal.backend.repository.UniversityMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@CrossOrigin(origins = "http://localhost:3001")
public class UniversityMasterController {

    @Autowired
    private UniversityMasterRepository universityMasterRepository;

    @GetMapping
    public List<UniversityMaster> getAllUniversities() {
        return universityMasterRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addUniversity(
            @RequestBody UniversityMaster university) {

        boolean exists =
                universityMasterRepository
                        .existsByUniversityNameIgnoreCase(
                                university.getUniversityName());

        if (exists) {
            return ResponseEntity
                    .badRequest()
                    .body("University already exists");
        }

        return ResponseEntity.ok(
                universityMasterRepository.save(university)
        );
    }
}