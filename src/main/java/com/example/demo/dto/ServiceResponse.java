package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private Long id;
    private String serviceName;
    private String category;
    private String description;
    private Double estimatedDuration;
    private BigDecimal basePrice;
    private String requiredSkills;
    private String priority;
    private Integer maxDailySlots;
    private Integer availableSlots;
    private String serviceImage;
    private Boolean isActive;
    private String additionalNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Availability tracking for today
    private Integer todayBookings;  // Number of bookings for today
    private Integer remainingSlots; // Remaining slots for today (maxDailySlots - todayBookings)
    private Boolean availableToday; // Whether service is available for booking today
}
