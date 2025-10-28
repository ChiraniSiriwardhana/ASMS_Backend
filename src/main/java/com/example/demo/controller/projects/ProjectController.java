package com.example.demo.controller.projects;

import com.example.demo.dto.projects.ProjectRequest;
import com.example.demo.dto.projects.ProjectResponse;
import com.example.demo.dto.projects.StatusUpdateRequest;
import com.example.demo.service.projects.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProjectResponse>> getCustomerProjects(@PathVariable Long customerId) {
        List<ProjectResponse> responses = projectService.getProjectsByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/customer/{customerId}")
    public ResponseEntity<ProjectResponse> getCustomerProject(
            @PathVariable Long id,
            @PathVariable Long customerId) {
        ProjectResponse response = projectService.getProjectByIdAndCustomerId(id, customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ProjectResponse>> getEmployeeProjects(@PathVariable Long employeeId) {
        List<ProjectResponse> responses = projectService.getProjectsByEmployeeId(employeeId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        ProjectResponse response = projectService.updateProjectStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign/{employeeId}")
    public ResponseEntity<ProjectResponse> assignEmployeeToProject(
            @PathVariable Long id,
            @PathVariable Long employeeId) {
        ProjectResponse response = projectService.assignEmployeeToProject(id, employeeId);
        return ResponseEntity.ok(response);
    }
}