package com.jobportal.backend.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import com.jobportal.backend.model.Application;
import com.jobportal.backend.model.Job;
import com.jobportal.backend.model.Resume;
import com.jobportal.backend.repository.ApplicationRepository;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jobportal.backend.dto.JobAnalyticsDTO;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private AiRankingService aiRankingService;

    @Autowired
    private NotificationService notificationService;

    // ─── Apply for Job ────────────────────────────────────────────────────────
    public Application applyForJob(Application application) {
        if (applicationRepository.existsByCandidateIdAndJobId(
                application.getCandidateId(), application.getJobId())) {
            throw new RuntimeException("You already applied for this job!");
        }
        return applicationRepository.save(application);
    }

    // ─── Candidate Applications ───────────────────────────────────────────────
    public List<Application> getMyApplications(Long candidateId) {
        return applicationRepository.findByCandidateId(candidateId);
    }

    // ─── Recruiter Applications ───────────────────────────────────────────────
    public List<Application> getJobApplications(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // ─── AI Analyze + Rank ────────────────────────────────────────────────────
    // Called when recruiter clicks "Run AI Analysis"
    // Fetches resume text from resumes table (saved during application)
    public List<Application> analyzeAndRankApplications(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        List<Application> applications = applicationRepository.findByJobId(jobId);

        // FIXED — only analyze candidates with no score yet
        for (Application application : applications) {

            // ✅ Skip if already has a score — don't re-analyze
            if (application.getAiScore() != null && application.getAiScore() > 0) {
                System.out.println("⏭️ Skipping " + application.getCandidateName() + " — already scored: " + application.getAiScore());
                continue;
            }

            String resumeText = resumeRepository
                    .findTopByUserIdOrderByIdDesc(application.getCandidateId())
                    .map(Resume::getExtractedText)
                    .orElse("");

            AiRankingService.ResumeMatchAnalysis analysis =
                    aiRankingService.analyzeResumeForJob(resumeText, job, application);
            applyAiAnalysis(application, analysis);
        }

        applicationRepository.saveAll(applications);
        return sortByAiScore(applications);
    }

    // ─── Return saved rankings without re-running Gemini ─────────────────────
    public List<Application> getRankedApplications(Long jobId) {
        return sortByAiScore(applicationRepository.findByJobId(jobId));
    }

    // ─── Counts ───────────────────────────────────────────────────────────────
    public long getApplicantCount(Long jobId) {
        return applicationRepository.countByJobId(jobId);
    }

    public long countByStatus(String status) {
        return applicationRepository.countByStatus(status);
    }

    // ─── Analytics ────────────────────────────────────────────────────────────
    public List<JobAnalyticsDTO> getJobWiseAnalytics(String recruiterEmail) {
        List<Job> jobs = jobRepository.findByPostedBy(recruiterEmail);
        List<JobAnalyticsDTO> analytics = new ArrayList<>();

        for (Job job : jobs) {
            long applicants  = applicationRepository.countByJobId(job.getId());
            long shortlisted = applicationRepository.countByJobIdAndStatus(job.getId(), "SHORTLISTED");
            long rejected    = applicationRepository.countByJobIdAndStatus(job.getId(), "REJECTED");
            long hired       = applicationRepository.countByJobIdAndStatus(job.getId(), "HIRED");

            analytics.add(new JobAnalyticsDTO(
                    job.getId(), job.getTitle(), applicants,
                    shortlisted, rejected, hired,
                    job.getVacancies(), job.getClosingDate(), job.getPostedAt()
            ));
        }
        return analytics;
    }

    // ─── Update Status ────────────────────────────────────────────────────────
    public Application updateStatus(Long applicationId, String status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found!"));
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with ID: " + applicationId));
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    // ─── Apply with Resume ────────────────────────────────────────────────────
    // This is the main apply flow — called when candidate submits application
    public Application applyWithResume(
            MultipartFile resumeFile,
            Long candidateId, Long jobId,
            String candidateName, String candidateEmail,
            String phone, String education,
            String collegeName, String universityName,
            String percentage, String gender,
            String dateOfBirth, String yearOfPassing,
            String coverLetter, String skills) {

        // 1. Duplicate check
        if (applicationRepository.existsByCandidateIdAndJobId(candidateId, jobId)) {
            throw new RuntimeException("You already applied for this job!");
        }

        // 2. Extract PDF text
        String resumeText   = "";
        String resumeFileName = "";
        try {
            resumeFileName = resumeFile.getOriginalFilename();
            PDDocument document = Loader.loadPDF(resumeFile.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            resumeText = stripper.getText(document);
            document.close();
            System.out.println("📄 PDF extracted: " + resumeText.length() + " chars for " + candidateName);
        } catch (Exception e) {
            System.out.println("⚠️ PDF extraction failed for " + candidateName + ": " + e.getMessage());
        }

        // 3. ✅ SAVE resume text to resumes table immediately
        //    so it's available when recruiter runs AI analysis later
        try {
            Resume resume = new Resume();
            resume.setUserId(candidateId);
            resume.setFileName(resumeFileName);
            resume.setFilePath(""); // no file storage, text-only
            resume.setExtractedText(resumeText.length() > 5000
                    ? resumeText.substring(0, 5000)  // cap at 5000 chars
                    : resumeText);
            resumeRepository.save(resume);
            System.out.println("✅ Resume text saved to DB for candidateId=" + candidateId);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to save resume to DB: " + e.getMessage());
            // Don't throw — application should still be saved
        }

        // 4. Fetch job for AI analysis
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

        // 5. Build Application object with all form data
        Application application = new Application();
        application.setCandidateId(candidateId);
        application.setJobId(jobId);
        application.setCandidateName(candidateName);
        application.setCandidateEmail(candidateEmail);
        application.setPhone(phone);
        application.setEducation(education);
        application.setCollegeName(collegeName);
        application.setUniversityName(universityName);
        application.setPercentage(percentage);
        application.setGender(gender);
        application.setDateOfBirth(dateOfBirth);
        application.setYearOfPassing(yearOfPassing);
        application.setCoverLetter(coverLetter);
        application.setResumeFileName(resumeFileName);
        application.setJobTitle(job.getTitle());
        application.setCompany(job.getCompany());

        // 6. ✅ Run Gemini AI analysis immediately on application submit
        //    Uses both PDF text + form data so score is accurate from day 1
        try {
            AiRankingService.ResumeMatchAnalysis analysis =
                    aiRankingService.analyzeResumeForJob(resumeText, job, application);
            applyAiAnalysis(application, analysis);
            System.out.println("✅ AI score for " + candidateName + ": " + analysis.score());
        } catch (Exception e) {
            System.out.println("⚠️ AI analysis failed on apply: " + e.getMessage());
            // Score stays null — recruiter can trigger manually
        }

        // 7. Save application
        Application saved = applicationRepository.save(application);

        // 8. Send notification (non-blocking)
        try {
            notificationService.createApplicationNotification(
                    candidateEmail, job.getTitle(), job.getCompany());
        } catch (Exception e) {
            System.out.println("⚠️ Notification failed: " + e.getMessage());
        }

        return saved;
    }

    // ─── Private helpers ──────────────────────────────────────────────────────
    private void applyAiAnalysis(Application app, AiRankingService.ResumeMatchAnalysis analysis) {
        app.setAiScore(analysis.score());
        app.setSkillsMatch(analysis.skillsMatch());
        app.setMissingSkills(analysis.missingSkills());
        app.setStrengths(analysis.strengths());
        app.setAiRecommendation(analysis.recommendation());
        app.setExperienceMatch(analysis.experienceMatch());
        app.setEducationMatch(analysis.educationMatch());
        app.setAiFeedback(analysis.feedback());
    }

    private List<Application> sortByAiScore(List<Application> applications) {
        return applications.stream()
                .sorted(this::compareByAiScoreDesc)
                .toList();
    }

    private int compareByAiScoreDesc(Application a, Application b) {
        int cmp = Double.compare(scoreOrZero(b.getAiScore()), scoreOrZero(a.getAiScore()));
        if (cmp != 0) return cmp;
        if (a.getAppliedAt() == null) return 1;
        if (b.getAppliedAt() == null) return -1;
        return b.getAppliedAt().compareTo(a.getAppliedAt());
    }

    private double scoreOrZero(Double score) {
        return score == null ? 0.0 : score;
    }
}
