package com.jobportal.backend.controller;

import com.jobportal.backend.model.SkillMaster;
import com.jobportal.backend.repository.SkillMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://localhost:3001")
public class SkillMasterController {

    @Autowired
    private SkillMasterRepository skillMasterRepository;

    @GetMapping
    public List<SkillMaster> getAllSkills() {
        return skillMasterRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> addSkill(
            @RequestBody SkillMaster skill) {

        boolean exists =
                skillMasterRepository
                        .existsBySkillNameIgnoreCase(
                                skill.getSkillName());

        if (exists) {
            return ResponseEntity
                    .badRequest()
                    .body("Skill already exists");
        }

        return ResponseEntity.ok(
                skillMasterRepository.save(skill)
        );
    }
}