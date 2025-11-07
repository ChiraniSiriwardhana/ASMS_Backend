package com.example.demo.dto.projects;

import com.example.demo.enums.ProjectStatus;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StatusUpdateRequest {
    @NotNull(message = "Status is required")
    private ProjectStatus status;

    private String updateMessage;
    private Long employeeId;
    private Integer percentageComplete;
}