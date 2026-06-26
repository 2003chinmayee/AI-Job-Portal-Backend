package com.jobportal.backend.controller;

import com.jobportal.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")  // ✅ Fixed — was only allowing 3001, now allows all
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

//    @GetMapping
//    public ResponseEntity<?> getAll(Authentication auth) {
//        return ResponseEntity.ok(notificationService.getUserNotifications(auth.getName()));
//    }

    @GetMapping
    public ResponseEntity<?> getAll(Authentication auth) {

        System.out.println("========== Notification API Called ==========");

        if (auth == null) {
            System.out.println("Authentication is NULL");
            return ResponseEntity.status(401).body("Authentication is null");
        }

        System.out.println("Logged in user: " + auth.getName());

        return ResponseEntity.ok(notificationService.getUserNotifications(auth.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication auth) {
        long count = notificationService.getUnreadCount(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.ok(Map.of("message", "All marked as read"));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markOneRead(@PathVariable Long id) {
        notificationService.markOneAsRead(id);
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }
}
