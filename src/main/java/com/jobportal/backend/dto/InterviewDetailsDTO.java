package com.jobportal.backend.dto;

import lombok.Data;

@Data
public class InterviewDetailsDTO {

    private Long applicationId;

    private String interviewDate;

    private String interviewTime;

    private String interviewMode;

    private String meetingLink;

    private Integer rounds;
}