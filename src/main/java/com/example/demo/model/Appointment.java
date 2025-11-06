package com.example.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Appointment fields
    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private String vehicleBrand;

    private String model;

    private String yearOfManufacture;

    @Column(nullable = false)
    private String registerNumber;

    private String fuelType;

    private String serviceCategory;

    private String serviceType;

    private String additionalRequirements;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @Column(nullable = false)
    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;  // Status of the appointment (Pending, Confirmed, In Progress, Completed)

    // Many appointments can belong to one user (CUSTOMER)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Employee assigned to this appointment
    @Column(name = "assigned_employee_id")
    private Long assignedEmployeeId;

    // Timestamps for appointment creation and updates
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
