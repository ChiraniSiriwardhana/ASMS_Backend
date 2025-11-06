package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedServiceDTO {
    private Long serviceId;
    private String serviceName;
    private String description;
    private Double price;
    private String duration;
    private LocalDateTime assignedDate;
    private String assignedBy; // Admin username
}

