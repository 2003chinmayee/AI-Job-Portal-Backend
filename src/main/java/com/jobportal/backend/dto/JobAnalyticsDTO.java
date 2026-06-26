package com.jobportal.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobAnalyticsDTO {

    private Long jobId;
    private String jobTitle;
    private Long applicants;
    private Long shortlisted;
    private Long rejected;
    private Long hired;

    // NEW FIELDS
    private Integer vacancies;
    private LocalDate closingDate;
    private LocalDateTime postedDate;
}