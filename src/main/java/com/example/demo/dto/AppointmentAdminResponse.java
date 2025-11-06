package com.example.demo.dto;

import com.example.demo.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentAdminResponse {
    private Long id;
    
    // Vehicle information
    private String vehicleType;
    private String vehicleBrand;
    private String model;
    private String yearOfManufacture;
    private String registerNumber;
    private String fuelType;
    
    // Service information
    private String serviceCategory;
    private String serviceType;
    private String additionalRequirements;
    
    // Appointment details
    private LocalDateTime appointmentDate;
    private String timeSlot;
    private AppointmentStatus status;
    
    // Customer information
    private String customerUsername;
    private String customerEmail;
    private String customerPhone;
    private String customerFirstName;
    private String customerLastName;
    
    // Assigned employee
    private Long assignedEmployeeId;
    private String assignedEmployeeName;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
