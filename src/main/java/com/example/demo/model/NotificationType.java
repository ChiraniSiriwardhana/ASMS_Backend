package com.example.demo.model;

public enum NotificationType {
    APPOINTMENT_CREATED,        // When customer creates an appointment
    APPOINTMENT_CONFIRMED,      // When admin confirms appointment
    APPOINTMENT_CANCELLED,      // When appointment is cancelled
    EMPLOYEE_ASSIGNED,          // When employee is assigned to appointment
    STATUS_CHANGED_IN_SERVICE,  // When status changes to IN_SERVICE
    STATUS_CHANGED_READY,       // When status changes to READY
    STATUS_CHANGED_COMPLETED,   // When status changes to COMPLETED
    GENERAL                     // General notification
}

