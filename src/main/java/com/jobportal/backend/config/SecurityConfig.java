package com.jobportal.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration       // Tells Spring "this is a config file"
@EnableWebSecurity   // Enables Spring Security
public class SecurityConfig {

    @Bean  // Spring manages this object for us
    public PasswordEncoder passwordEncoder() {
        // BCrypt is the best password encryption algorithm
        // "mypassword" → "$2a$10$randomhash..."
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/resumes/**",
                                "/api/gemini/**",
                                "/api/jobs/**",
                                "/api/applications/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
