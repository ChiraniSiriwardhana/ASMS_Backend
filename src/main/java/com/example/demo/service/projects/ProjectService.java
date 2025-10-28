package com.example.demo.service.projects;

import com.example.demo.dto.projects.ProjectRequest;
import com.example.demo.dto.projects.ProjectResponse;
import com.example.demo.dto.projects.StatusUpdateRequest;
import com.example.demo.model.projects.Project;
import com.example.demo.model.projects.ProgressUpdate;
import com.example.demo.enums.ProjectStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.projects.ProjectRepository;
import com.example.demo.repository.projects.ProgressUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProgressUpdateRepository progressUpdateRepository;
    private final WebSocketService webSocketService;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setCustomerId(request.getCustomerId());
        project.setVehicleId(request.getVehicleId());
        project.setServiceType(request.getServiceType());
        project.setEstimatedCost(request.getEstimatedCost());
        project.setStatus(ProjectStatus.RECEIVED);

        Project savedProject = projectRepository.save(project);
        return mapToProjectResponse(savedProject);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return mapToProjectResponse(project);
    }

    public ProjectResponse getProjectByIdAndCustomerId(Long id, Long customerId) {
        Project project = projectRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return mapToProjectResponse(project);
    }

    public List<ProjectResponse> getProjectsByCustomerId(Long customerId) {
        return projectRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByEmployeeId(Long employeeId) {
        return projectRepository.findByAssignedEmployeeId(employeeId)
                .stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse updateProjectStatus(Long projectId, StatusUpdateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setStatus(request.getStatus());

        if (request.getUpdateMessage() != null && !request.getUpdateMessage().trim().isEmpty()) {
            ProgressUpdate progressUpdate = new ProgressUpdate();
            progressUpdate.setProject(project);
            progressUpdate.setMessage(request.getUpdateMessage());
            progressUpdate.setEmployeeId(request.getEmployeeId());
            progressUpdate.setPercentageComplete(request.getPercentageComplete());
            progressUpdateRepository.save(progressUpdate);
        }

        Project updatedProject = projectRepository.save(project);

        // Send real-time update
        webSocketService.sendProjectUpdate(updatedProject.getCustomerId(), mapToProjectResponse(updatedProject));

        return mapToProjectResponse(updatedProject);
    }

    @Transactional
    public ProjectResponse assignEmployeeToProject(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setAssignedEmployeeId(employeeId);
        Project updatedProject = projectRepository.save(project);

        return mapToProjectResponse(updatedProject);
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setCustomerId(project.getCustomerId());
        response.setVehicleId(project.getVehicleId());
        response.setStatus(project.getStatus());
        response.setServiceType(project.getServiceType());
        response.setAssignedEmployeeId(project.getAssignedEmployeeId());
        response.setEstimatedCost(project.getEstimatedCost());
        response.setActualCost(project.getActualCost());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }
}