package com.example.demo.dto;

import com.example.demo.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Long recipientId;
    private Long appointmentId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

