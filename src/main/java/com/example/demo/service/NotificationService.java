package com.example.demo.service;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.dto.WebSocketNotificationMessage;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Create and send a notification to a specific user via WebSocket
     */
    @Transactional
    public Notification createAndSendNotification(Long recipientId, Long appointmentId,
                                                   String title, String message,
                                                   NotificationType type) {
        // Save notification to database
        Notification notification = Notification.builder()
                .recipientId(recipientId)
                .appointmentId(appointmentId)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        sendWebSocketNotification(recipientId, notification);

        return notification;
    }

    /**
     * Send WebSocket notification to a specific user
     */
    private void sendWebSocketNotification(Long recipientId, Notification notification) {
        WebSocketNotificationMessage wsMessage = WebSocketNotificationMessage.builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .appointmentId(notification.getAppointmentId())
                .recipientId(recipientId)
                .timestamp(notification.getCreatedAt())
                .build();

        // Send to user-specific topic
        messagingTemplate.convertAndSend("/topic/notifications/user." + recipientId, wsMessage);
    }

    /**
     * Notify customer about appointment updates
     */
    @Transactional
    public void notifyCustomer(Long customerId, Long appointmentId, String title,
                                String message, NotificationType type) {
        createAndSendNotification(customerId, appointmentId, title, message, type);
    }

    /**
     * Notify employee about appointment assignment
     */
    @Transactional
    public void notifyEmployee(Long employeeId, Long appointmentId, String title,
                                String message, NotificationType type) {
        createAndSendNotification(employeeId, appointmentId, title, message, type);
    }

    /**
     * Notify all admins about appointment events
     */
    @Transactional
    public void notifyAdmins(Long appointmentId, String title, String message,
                             NotificationType type) {
        // Find all admin users
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        for (User admin : admins) {
            createAndSendNotification(admin.getId(), appointmentId, title, message, type);
        }

        // Also send to admin broadcast topic
        WebSocketNotificationMessage wsMessage = WebSocketNotificationMessage.builder()
                .title(title)
                .message(message)
                .type(type)
                .appointmentId(appointmentId)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/admin", wsMessage);
    }

    /**
     * Get all notifications for a user
     */
    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for a user
     */
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notification count
     */
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Verify the notification belongs to the user
        if (!notification.getRecipientId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to notification");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        LocalDateTime now = LocalDateTime.now();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * Delete a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        // Verify the notification belongs to the user
        if (!notification.getRecipientId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
    }

    /**
     * Convert Notification entity to DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .recipientId(notification.getRecipientId())
                .appointmentId(notification.getAppointmentId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}

