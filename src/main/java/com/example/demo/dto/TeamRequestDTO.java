package com.example.demo.dto;

import com.example.demo.model.Team.Specialization;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class TeamRequestDTO {
    @NotBlank(message = "Team name is required")
    @Size(min = 2, message = "Team name must be at least 2 characters long")
    private String name;

    @NotNull(message = "Specialization is required")
    private Specialization specialization;

    @Min(value = 0, message = "Member count cannot be negative")
    private Integer memberCount;

    @Min(value = 0, message = "Working hours cannot be negative")
    private Integer totalWorkingHours;

    @Min(value = 18, message = "Average age must be at least 18")
    @Max(value = 65, message = "Average age cannot exceed 65")
    private Integer averageAge;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;
}