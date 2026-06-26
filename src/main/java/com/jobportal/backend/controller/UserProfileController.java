package com.jobportal.backend.controller;

import com.jobportal.backend.model.User;
import com.jobportal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3001")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    private Map<String, Object> buildResponse(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",         user.getId());
        map.put("name",       user.getName()       != null ? user.getName()       : "");
        map.put("email",      user.getEmail()      != null ? user.getEmail()      : "");
        map.put("phone",      user.getPhone()      != null ? user.getPhone()      : "");
        map.put("location",   user.getLocation()   != null ? user.getLocation()   : "");
        map.put("bio",        user.getBio()        != null ? user.getBio()        : "");
        map.put("skills",     user.getSkills()     != null ? user.getSkills()     : "");
        map.put("education",  user.getEducation()  != null ? user.getEducation()  : "");
        map.put("experience", user.getExperience() != null ? user.getExperience() : "");
        map.put("resumeUrl",  user.getResumeUrl()  != null ? user.getResumeUrl()  : "");
        return map;
    }

    // GET /api/profile
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(buildResponse(user));
    }

    // PUT /api/profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body,
                                           Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        if (body.containsKey("name"))       user.setName(body.get("name"));
        if (body.containsKey("phone"))      user.setPhone(body.get("phone"));
        if (body.containsKey("location"))   user.setLocation(body.get("location"));
        if (body.containsKey("bio"))        user.setBio(body.get("bio"));
        if (body.containsKey("skills"))     user.setSkills(body.get("skills"));
        if (body.containsKey("education"))  user.setEducation(body.get("education"));
        if (body.containsKey("experience")) user.setExperience(body.get("experience"));
        if (body.containsKey("resumeUrl"))  user.setResumeUrl(body.get("resumeUrl"));
        userRepository.save(user);
        return ResponseEntity.ok(buildResponse(user));
    }
}
