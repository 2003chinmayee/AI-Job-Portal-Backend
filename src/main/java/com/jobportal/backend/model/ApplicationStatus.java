package com.jobportal.backend.model;

 // ← Change "yourpackage" to your actual package name

public enum ApplicationStatus {
    APPLIED,       // Just applied, no action taken
    SHORTLISTED,   // Recruiter shortlisted them → interview email sent
    REJECTED,      // Recruiter rejected them → rejection email sent
    HIRED          // Recruiter hired them → offer letter email sent
}