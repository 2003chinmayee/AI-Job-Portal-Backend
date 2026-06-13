package com.jobportal.backend.repository;

import com.jobportal.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository  // Tells Spring "this talks to database"
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> means:
    // User = which table to work with
    // Long = data type of primary key (id)

    // Spring reads this method name and writes SQL automatically!
    // Becomes: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Becomes: SELECT * FROM users WHERE email = ? (returns true/false)
    boolean existsByEmail(String email);
}