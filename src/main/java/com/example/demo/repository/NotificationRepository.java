package com.example.demo.repository;

import com.example.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a specific user
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    // Find unread notifications for a user
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

    // Find notifications by appointment
    List<Notification> findByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);

    // Count unread notifications for a user
    long countByRecipientIdAndIsReadFalse(Long recipientId);
}

