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
public class WebSocketNotificationMessage {
    private Long notificationId;
    private String title;
    private String message;
    private NotificationType type;
    private Long appointmentId;
    private Long recipientId;
    private LocalDateTime timestamp;
}

