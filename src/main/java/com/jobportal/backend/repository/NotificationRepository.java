package com.jobportal.backend.repository;

import com.jobportal.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    long countByUserEmailAndIsReadFalse(String email);

    List<Notification> findByUserEmailAndIsReadFalse(String email);
}