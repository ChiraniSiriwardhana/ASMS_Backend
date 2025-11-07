package com.example.demo.dto.projects;

import com.example.demo.enums.ProjectStatus;
import com.example.demo.enums.ServiceType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private Long customerId;
    private Long vehicleId;
    private ProjectStatus status;
    private ServiceType serviceType;
    private Long assignedEmployeeId;
    private Double estimatedCost;
    private Double actualCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}