package com.jobportal.backend.service;

import com.jobportal.backend.model.Notification;
import com.jobportal.backend.model.User;
import com.jobportal.backend.repository.NotificationRepository;
import com.jobportal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Call this when user applies for a job.
    // IMPORTANT: this must never throw and break the apply flow — a missing
    // notification is not worth failing the whole application submission.
    public void createApplicationNotification(String userEmail, String jobTitle, String companyName) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            // Log it so it's visible in the backend console, but do NOT throw.
            System.out.println("⚠️ Notification skipped: no user found with email '" + userEmail + "'");
            return;
        }

        User user = userOpt.get();
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle("Application Submitted ✅");
        n.setMessage("You applied for " + jobTitle + " at " + companyName);
        n.setType("APPLICATION_SUBMITTED");
        notificationRepository.save(n);
    }

    // Call this when recruiter changes application status.
    // Same safety: a notification failure shouldn't break a status update.
    public void createStatusUpdateNotification(String userEmail, String jobTitle, String newStatus) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            System.out.println("⚠️ Notification skipped: no user found with email '" + userEmail + "'");
            return;
        }

        User user = userOpt.get();
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle("Status Updated 📢");
        n.setMessage("Your application for " + jobTitle + " is now: " + newStatus);
        n.setType("STATUS_UPDATE");
        notificationRepository.save(n);
    }

    public List<Notification> getUserNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public long getUnreadCount(String email) {
        return notificationRepository.countByUserEmailAndIsReadFalse(email);
    }

    public void markAllAsRead(String email) {
        List<Notification> unread = notificationRepository.findByUserEmailAndIsReadFalse(email);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void markOneAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
