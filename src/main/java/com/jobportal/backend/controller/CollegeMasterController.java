package com.jobportal.backend.controller;

import com.jobportal.backend.model.CollegeMaster;
import com.jobportal.backend.repository.CollegeMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
@CrossOrigin(origins = "http://localhost:3001")
public class CollegeMasterController {

    @Autowired
    private CollegeMasterRepository collegeMasterRepository;

    @GetMapping
    public List<CollegeMaster> getAllColleges() {
        return collegeMasterRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addCollege(
            @RequestBody CollegeMaster college) {

        boolean exists =
                collegeMasterRepository
                        .existsByCollegeNameIgnoreCase(
                                college.getCollegeName());

        if (exists) {
            return ResponseEntity
                    .badRequest()
                    .body("College already exists");
        }

        return ResponseEntity.ok(
                collegeMasterRepository.save(college)
        );
    }
}