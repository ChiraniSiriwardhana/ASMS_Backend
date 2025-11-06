package com.example.demo.model;


public enum AppointmentStatus {
    PENDING,      // Appointment is created, but not yet approved by admin
    CONFIRMED,    // Appointment is approved by admin
    IN_SERVICE,   // Appointment is being serviced (formerly IN_PROGRESS)
    READY,        // Service completed, ready for pickup
    COMPLETED,    // Appointment has been completed and customer picked up
    CANCELLED     // Appointment was rejected or cancelled
}
