package com.jobportal.backend.service;

import com.jobportal.backend.model.Application;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.sender.email}")
    private String senderEmail;

    @Value("${mail.sender.name}")
    private String senderName;

    // ─── CORE SEND METHOD ────────────────────────────────────────────
    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderEmail, senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    // ─── PUBLIC HELPER ───────────────────────────────────────────────
    public void sendMail(String toEmail, String subject, String body) {
        sendHtmlEmail(toEmail, subject, body);
    }

    // ─── 1. SHORTLIST EMAIL ──────────────────────────────────────────
    public void sendShortlistEmail(Application app) {
        String recruiterEmail = app.getContactEmail();
        String recruiterName  = app.getContactPerson() != null ? app.getContactPerson() : "Recruitment Team";

        String subject = "🎉 You've been Shortlisted — " + app.getJobTitle() + " at " + app.getCompany();

        String body = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:620px;margin:auto;
                        border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;">
                <div style="background:linear-gradient(135deg,#4F46E5,#7C3AED);padding:36px 30px;text-align:center;">
                    <div style="font-size:40px;margin-bottom:10px;">🎉</div>
                    <h1 style="color:white;margin:0;font-size:24px;">Congratulations!</h1>
                    <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;">You have been shortlisted</p>
                </div>
                <div style="padding:32px 30px;background:#ffffff;">
                    <p style="font-size:16px;color:#111827;">Dear <strong>%s</strong>,</p>
                    <p style="font-size:15px;color:#374151;line-height:1.6;">
                        Your application for <strong>%s</strong> at <strong>%s</strong>
                        has been shortlisted for the next round!
                    </p>
                    <div style="background:#F5F3FF;border-radius:10px;padding:20px 24px;margin:24px 0;">
                        <h3 style="color:#4F46E5;margin:0 0 14px;">📋 Job Details</h3>
                        <table style="width:100%%;font-size:14px;">
                            <tr><td style="color:#6B7280;width:140px;padding:6px 0;">🏢 Company</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">💼 Position</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">📅 Interview Date</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">⏰ Interview Time</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">💻 Mode</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                        </table>
                    </div>
                    <div style="background:#F0FDF4;border-radius:10px;padding:16px 24px;margin:24px 0;">
                        <h3 style="color:#16A34A;margin:0 0 10px;">📞 Point of Contact</h3>
                        <p style="margin:4px 0;font-size:14px;">👤 <strong>%s</strong></p>
                        <p style="margin:4px 0;font-size:14px;">📧 %s</p>
                    </div>
                    <p style="font-size:15px;color:#374151;">Best Regards,<br><strong>%s Recruitment Team</strong></p>
                </div>
                <div style="background:#F9FAFB;padding:16px 30px;text-align:center;border-top:1px solid #E5E7EB;">
                    <p style="color:#9CA3AF;font-size:12px;margin:0;">Sent via <strong>AI Job Portal</strong></p>
                </div>
            </div>
            """.formatted(
                app.getCandidateName(),
                app.getJobTitle(), app.getCompany(),
                app.getCompany(), app.getJobTitle(),
                app.getInterviewDate() != null ? app.getInterviewDate() : "To be confirmed",
                app.getInterviewTime() != null ? app.getInterviewTime() : "To be confirmed",
                app.getInterviewMode() != null ? app.getInterviewMode() : "To be confirmed",
                recruiterName,
                recruiterEmail != null ? recruiterEmail : "",
                app.getCompany()
        );

        sendHtmlEmail(app.getCandidateEmail(), subject, body);
    }

    // ─── 2. REJECTION EMAIL ──────────────────────────────────────────
    public void sendRejectionEmail(Application app) {
        String subject = "Application Update — " + app.getJobTitle() + " at " + app.getCompany();

        String body = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:620px;margin:auto;
                        border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;">
                <div style="background:linear-gradient(135deg,#6B7280,#4B5563);padding:36px 30px;text-align:center;">
                    <div style="font-size:40px;margin-bottom:10px;">📋</div>
                    <h1 style="color:white;margin:0;font-size:24px;">Application Status Update</h1>
                    <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;">%s at %s</p>
                </div>
                <div style="padding:32px 30px;background:#ffffff;">
                    <p style="font-size:16px;color:#111827;">Dear <strong>%s</strong>,</p>
                    <p style="font-size:15px;color:#374151;line-height:1.6;">
                        Thank you for applying for <strong>%s</strong> at <strong>%s</strong>.
                        After careful consideration, we will not be moving forward with your application at this time.
                    </p>
                    <div style="background:#FFFBEB;border-left:4px solid #F59E0B;border-radius:8px;padding:18px 20px;margin:24px 0;">
                        <p style="margin:0;font-size:14px;color:#374151;">
                            💡 <strong>Don't be discouraged!</strong> Keep building your skills and apply for future openings!
                        </p>
                    </div>
                    <p style="font-size:15px;color:#374151;">
                        We wish you all the best.<br><br>
                        Warm Regards,<br><strong>%s Recruitment Team</strong>
                    </p>
                </div>
                <div style="background:#F9FAFB;padding:16px 30px;text-align:center;border-top:1px solid #E5E7EB;">
                    <p style="color:#9CA3AF;font-size:12px;margin:0;">Sent via <strong>AI Job Portal</strong></p>
                </div>
            </div>
            """.formatted(
                app.getJobTitle(), app.getCompany(),
                app.getCandidateName(),
                app.getJobTitle(), app.getCompany(),
                app.getCompany()
        );

        sendHtmlEmail(app.getCandidateEmail(), subject, body);
    }

    // ─── 3. HIRE EMAIL ───────────────────────────────────────────────
    public void sendHireEmail(Application app) {
        String recruiterEmail = app.getContactEmail();
        String recruiterName  = app.getContactPerson() != null ? app.getContactPerson() : "HR Team";

        String subject = "🎊 Official Offer Letter — " + app.getJobTitle() + " at " + app.getCompany();

        String body = """
            <div style="font-family:'Segoe UI',Arial,sans-serif;max-width:620px;margin:auto;
                        border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;">
                <div style="background:linear-gradient(135deg,#16A34A,#15803D);padding:36px 30px;text-align:center;">
                    <div style="font-size:40px;margin-bottom:10px;">🎊</div>
                    <h1 style="color:white;margin:0;font-size:24px;">Welcome to the Team!</h1>
                    <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;">Official Offer from %s</p>
                </div>
                <div style="padding:32px 30px;background:#ffffff;">
                    <p style="font-size:16px;color:#111827;">Dear <strong>%s</strong>,</p>
                    <p style="font-size:15px;color:#374151;line-height:1.6;">
                        We are thrilled to officially welcome you to <strong>%s</strong>!
                    </p>
                    <div style="background:#F0FDF4;border-radius:10px;padding:20px 24px;margin:24px 0;">
                        <h3 style="color:#16A34A;margin:0 0 14px;">📄 Offer Details</h3>
                        <table style="width:100%%;font-size:14px;">
                            <tr><td style="color:#6B7280;width:140px;padding:6px 0;">🏢 Company</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">💼 Position</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">💰 Salary</td>
                                <td style="color:#16A34A;font-weight:700;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">📅 Joining Date</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                            <tr><td style="color:#6B7280;padding:6px 0;">📍 Location</td>
                                <td style="color:#111827;font-weight:600;">%s</td></tr>
                        </table>
                    </div>
                    <div style="background:#EEF2FF;border-radius:10px;padding:16px 24px;margin:24px 0;">
                        <h3 style="color:#4F46E5;margin:0 0 10px;">📞 HR Contact</h3>
                        <p style="margin:4px 0;font-size:14px;">👤 <strong>%s</strong></p>
                        <p style="margin:4px 0;font-size:14px;">📧 %s</p>
                    </div>
                    <p style="font-size:15px;color:#374151;">
                        Please reply to confirm your acceptance. We look forward to having you! 🎉<br><br>
                        Best Regards,<br><strong>%s HR Team</strong>
                    </p>
                </div>
                <div style="background:#F9FAFB;padding:16px 30px;text-align:center;border-top:1px solid #E5E7EB;">
                    <p style="color:#9CA3AF;font-size:12px;margin:0;">Sent via <strong>AI Job Portal</strong></p>
                </div>
            </div>
            """.formatted(
                app.getCompany(),
                app.getCandidateName(),
                app.getCompany(),
                app.getCompany(), app.getJobTitle(),
                app.getSalary()         != null ? app.getSalary()         : "As discussed",
                app.getJoiningDate()    != null ? app.getJoiningDate()    : "To be confirmed",
                app.getOfficeLocation() != null ? app.getOfficeLocation() : "To be confirmed",
                recruiterName,
                recruiterEmail != null ? recruiterEmail : "",
                app.getCompany()
        );

        sendHtmlEmail(app.getCandidateEmail(), subject, body);
    }
}