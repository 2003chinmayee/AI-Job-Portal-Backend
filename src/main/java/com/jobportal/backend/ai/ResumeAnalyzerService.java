
package com.jobportal.backend.ai;

import org.springframework.stereotype.Service;

@Service
public class ResumeAnalyzerService {

    public String analyzeResume(String resumeText) {

        if (resumeText.contains("Java")
                && resumeText.contains("Spring Boot")) {

            return """
                    Resume Score: 85

                    Strong Skills:
                    Java
                    Spring Boot

                    Missing Skills:
                    Docker
                    AWS

                    Recommendation:
                    Learn Docker
                    Learn AWS
                    Add deployment projects
                    """;
        }

        return """
                Resume Score: 60

                Recommendation:
                Add Java projects
                Add Spring Boot projects
                Improve resume content
                """;
    }
}