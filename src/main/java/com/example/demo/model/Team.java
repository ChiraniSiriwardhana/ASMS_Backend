package com.example.demo.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "teams")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialization specialization;

    @Column(name = "member_count")
    private Integer memberCount;

    @Column(name = "total_working_hours")
    private Integer totalWorkingHours;

    @Column(name = "average_age")
    private Integer averageAge;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User employee;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public enum Specialization {
        ENGINE,
        TRANSMISSION,
        ELECTRICAL,
        BRAKES,
        SUSPENSION,
        DIAGNOSTICS,
        BODYWORK,
        PAINTING,
        INTERIOR,
        QUALITY_CONTROL,
        OTHER
    }
}