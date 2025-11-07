package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double estimatedDuration; // in hours

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    private String requiredSkills;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    private Integer maxDailySlots;

    @Builder.Default
    @Column(nullable = false)
    private Integer availableSlots = 0; // Tracks remaining slots for today

    private String serviceImage;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String additionalNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
