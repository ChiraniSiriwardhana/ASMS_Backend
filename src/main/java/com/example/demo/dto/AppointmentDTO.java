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
public class AppointmentDTO {

    // For employee dashboard
    private Long id;
    private Long customerId;
    private String customerName;
    private Long serviceId;
    private String serviceName;
    private Long employeeId;
    private String employeeName;
    private String status;
    private String notes;

    // Original fields for customer appointment creation
    private String vehicleType;
    private String vehicleBrand;
    private String model;
    private String yearOfManufacture;
    private String registerNumber;
    private String fuelType;
    private String serviceCategory;
    private String serviceType;
    private String additionalRequirements;
    private LocalDateTime appointmentDate;
    private String timeSlot;

    // If needed, you can add userId here to associate appointments with the logged-in user
    private Long userId;  // This can be useful if you are passing data from frontend and need to associate it manually
}
