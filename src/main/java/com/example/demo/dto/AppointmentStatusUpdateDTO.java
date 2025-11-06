package com.example.demo.dto;

import com.example.demo.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusUpdateDTO {
    private AppointmentStatus status;  // Uses existing enum: PENDING, CONFIRMED, IN_SERVICE, READY, COMPLETED, CANCELLED
    private Integer progress;           // Optional: 0-100
    private String completedDate;       // Optional: completion date
}

