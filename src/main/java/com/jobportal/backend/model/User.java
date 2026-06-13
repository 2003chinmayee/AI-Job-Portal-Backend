package com.jobportal.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                    // Lombok: auto creates getters, setters
@NoArgsConstructor       // Lombok: auto creates empty constructor
@AllArgsConstructor      // Lombok: auto creates full constructor
@Entity                  // This class = database table
@Table(name = "users")   // Table name in MySQL
public class User {

    @Id                                                    // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // Auto increment
    private Long id;

    @Column(nullable = false)         // This column cannot be empty
    private String name;

    @Column(unique = true, nullable = false)   // Must be unique, cannot be empty
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;   // CANDIDATE or RECRUITER

    private String phone;

    private String location;
}