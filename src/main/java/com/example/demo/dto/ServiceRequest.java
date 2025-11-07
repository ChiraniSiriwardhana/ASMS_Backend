package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Category is required")
    private String category;

    private String description;

    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Duration must be positive")
    private Double estimatedDuration;

    @NotNull(message = "Base price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal basePrice;

    private String requiredSkills;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotNull(message = "Max daily slots is required")
    @Positive(message = "Max daily slots must be positive")
    private Integer maxDailySlots;

    private String serviceImage;

    private String additionalNotes;
}
