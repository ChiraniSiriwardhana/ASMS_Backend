package com.example.demo.service;

import com.example.demo.dto.TeamRequestDTO;
import com.example.demo.dto.TeamResponseDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Team;
import com.example.demo.model.User;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamResponseDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return convertToDTO(team);
    }

    @Transactional
    public TeamResponseDTO createTeam(TeamRequestDTO teamRequest) {
        // Validate employee exists if provided
        User employee = null;
        if (teamRequest.getEmployeeId() != null) {
            employee = userRepository.findById(teamRequest.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + teamRequest.getEmployeeId()));
        }

        // Check if team name already exists
        if (teamRepository.existsByName(teamRequest.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + teamRequest.getName());
        }

        Team team = new Team();
        team.setName(teamRequest.getName());
        team.setSpecialization(teamRequest.getSpecialization());
        team.setMemberCount(teamRequest.getMemberCount());
        team.setTotalWorkingHours(teamRequest.getTotalWorkingHours());
        team.setAverageAge(teamRequest.getAverageAge());
        team.setDescription(teamRequest.getDescription());
        team.setEmployee(employee);

        Team savedTeam = teamRepository.save(team);
        return convertToDTO(savedTeam);
    }

    @Transactional
    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO teamRequest) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        // Validate employee exists if provided
        User employee = null;
        if (teamRequest.getEmployeeId() != null) {
            employee = userRepository.findById(teamRequest.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + teamRequest.getEmployeeId()));
        }

        // Check if team name already exists (excluding current team)
        if (teamRepository.existsByName(teamRequest.getName()) &&
                !team.getName().equals(teamRequest.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + teamRequest.getName());
        }

        team.setName(teamRequest.getName());
        team.setSpecialization(teamRequest.getSpecialization());
        team.setMemberCount(teamRequest.getMemberCount());
        team.setTotalWorkingHours(teamRequest.getTotalWorkingHours());
        team.setAverageAge(teamRequest.getAverageAge());
        team.setDescription(teamRequest.getDescription());
        team.setEmployee(employee);

        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        teamRepository.delete(team);
    }

    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getTeamsByEmployee(Long employeeId) {
        // Validate employee exists
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        return teamRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getTeamsBySpecialization(Team.Specialization specialization) {
        return teamRepository.findBySpecialization(specialization).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private TeamResponseDTO convertToDTO(Team team) {
        TeamResponseDTO.TeamResponseDTOBuilder builder = TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .specialization(team.getSpecialization())
                .memberCount(team.getMemberCount())
                .totalWorkingHours(team.getTotalWorkingHours())
                .averageAge(team.getAverageAge())
                .description(team.getDescription())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt());

        // Safely handle nullable employee
        if (team.getEmployee() != null) {
            builder.employeeId(team.getEmployee().getId());

            // Build employee name safely
            String employeeName = buildEmployeeName(team.getEmployee());
            builder.employeeName(employeeName);
        } else {
            builder.employeeId(null)
                    .employeeName("No supervisor assigned");
        }

        return builder.build();
    }

    private String buildEmployeeName(User employee) {
        if (employee == null) {
            return "No supervisor";
        }

        StringBuilder nameBuilder = new StringBuilder();

        if (employee.getFirstName() != null && !employee.getFirstName().trim().isEmpty()) {
            nameBuilder.append(employee.getFirstName().trim());
        }

        if (employee.getLastName() != null && !employee.getLastName().trim().isEmpty()) {
            if (nameBuilder.length() > 0) {
                nameBuilder.append(" ");
            }
            nameBuilder.append(employee.getLastName().trim());
        }

        // If both first and last name are null/empty, use username or email
        if (nameBuilder.length() == 0) {
            if (employee.getUsername() != null && !employee.getUsername().trim().isEmpty()) {
                nameBuilder.append(employee.getUsername().trim());
            } else if (employee.getEmail() != null && !employee.getEmail().trim().isEmpty()) {
                nameBuilder.append(employee.getEmail().trim());
            } else {
                nameBuilder.append("Unknown Employee");
            }
        }

        return nameBuilder.toString();
    }
}