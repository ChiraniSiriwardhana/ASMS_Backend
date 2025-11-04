package com.example.demo.dto;

import com.example.demo.model.Team.Specialization;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class TeamResponseDTO {
    private Long id;
    private String name;
    private Specialization specialization;
    private Integer memberCount;
    private Integer totalWorkingHours;
    private Integer averageAge;
    private String description;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}