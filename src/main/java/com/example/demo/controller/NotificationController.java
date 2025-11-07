package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.NotificationDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all notifications for logged-in user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getAllNotifications() {
        Long userId = getAuthenticatedUserId();
        List<NotificationDTO> notifications = notificationService.getNotificationsByUser(userId);

        return ResponseEntity.ok(ApiResponse.<List<NotificationDTO>>builder()
                .success(true)
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    /**
     * Get unread notifications for logged-in user
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications() {
        Long userId = getAuthenticatedUserId();
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(ApiResponse.<List<NotificationDTO>>builder()
                .success(true)
                .message("Unread notifications retrieved successfully")
                .data(notifications)
                .build());
    }

    /**
     * Get unread notification count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        Long userId = getAuthenticatedUserId();
        long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Unread count retrieved successfully")
                .data(count)
                .build());
    }

    /**
     * Mark a specific notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long notificationId) {
        Long userId = getAuthenticatedUserId();
        notificationService.markAsRead(notificationId, userId);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Notification marked as read")
                .build());
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        Long userId = getAuthenticatedUserId();
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("All notifications marked as read")
                .build());
    }

    /**
     * Delete a specific notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long notificationId) {
        Long userId = getAuthenticatedUserId();
        notificationService.deleteNotification(notificationId, userId);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Notification deleted successfully")
                .build());
    }

    /**
     * Get authenticated user ID from SecurityContext
     */
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    /**
     * Global exception handler for this controller
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        e.printStackTrace(); // Log the error

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Error: " + e.getMessage())
                        .data(null)
                        .build());
    }
}