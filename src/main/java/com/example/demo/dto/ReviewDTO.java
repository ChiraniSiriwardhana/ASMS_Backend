package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long appointmentId;
    private String username;  // optional if you want to show who gave the review
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
