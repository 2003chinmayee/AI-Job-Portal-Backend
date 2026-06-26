package com.jobportal.backend.service;

import com.jobportal.backend.config.JwtUtil;
import com.jobportal.backend.model.User;
import com.jobportal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // REGISTER
    public User registerUser(User user) {

        // ─── Duplicate email check ────────────────────────────────────
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered! Please login.");
        }

        // ─── Duplicate phone check ────────────────────────────────────
        if (user.getPhone() != null && !user.getPhone().isBlank()) {
            if (userRepository.existsByPhone(user.getPhone())) {
                throw new RuntimeException("Phone number already registered!");
            }
        }

        // ─── Phone format check (10 digits) ──────────────────────────
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new RuntimeException("Phone number is required!");
        }
        if (!user.getPhone().matches("^[0-9]{10}$")) {
            throw new RuntimeException("Phone number must be exactly 10 digits!");
        }

        // ─── Password strength check ──────────────────────────────────
        String password = user.getPassword();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            throw new RuntimeException(
                    "Password must be at least 8 characters and include: " +
                            "one uppercase letter, one lowercase letter, " +
                            "one number, and one special character (@$!%*?&)."
            );
        }

        // ─── Save ─────────────────────────────────────────────────────
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    // LOGIN
    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email!"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Incorrect password! Please try again.");
        }

        return jwtUtil.generateToken(email);
    }

    // GET user by email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }
}