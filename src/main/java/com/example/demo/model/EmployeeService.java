package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_services")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private User assignedBy; // Admin who assigned

    @Column(name = "assigned_date")
    private LocalDateTime assignedDate;

    @PrePersist
    protected void onCreate() {
        assignedDate = LocalDateTime.now();
    }
}

