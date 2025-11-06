package com.example.demo.model;


public enum AppointmentStatus {
    PENDING,      // Appointment is created, but not yet handled
    CONFIRMED,    // Appointment is confirmed by the service provider
    IN_PROGRESS,  // Appointment is being serviced
    COMPLETED,    // Appointment has been completed
    CANCELLED      // Appointment has been canceled
}
