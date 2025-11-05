package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.TeamMemberDTO;
import com.example.demo.service.TeamMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
public class TeamMemberController {

    @Autowired
    private TeamMemberService teamMemberService;

    @GetMapping("/allteam")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getAllTeamMembers() {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getAllTeamMembers();
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> getTeamMemberById(@PathVariable Long id) {
        try {
            return teamMemberService.getTeamMemberById(id)
                    .map(teamMember -> ResponseEntity.ok(ApiResponse.success(teamMember, "Team member retrieved successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team member: " + e.getMessage()));
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersByTeamId(@PathVariable Long teamId) {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersByTeamId(teamId);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved successfully for team: " + teamId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members: " + e.getMessage()));
        }
    }

    @GetMapping("/supervisor/{supervisorId}")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersBySupervisor(@PathVariable Long supervisorId) {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersBySupervisor(supervisorId);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved successfully for supervisor"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by supervisor: " + e.getMessage()));
        }
    }

    @GetMapping("/no-supervisor")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersWithoutSupervisor() {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersWithoutSupervisor();
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members without supervisor retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members without supervisor: " + e.getMessage()));
        }
    }

    @GetMapping("/no-team")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersWithoutTeam() {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersWithoutTeam();
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members without team retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members without team: " + e.getMessage()));
        }
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersBySpecialization(
            @PathVariable String specialization) {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersBySpecialization(specialization);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved by specialization"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid specialization: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by specialization: " + e.getMessage()));
        }
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersByCity(@PathVariable String city) {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersByCity(city);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved by city"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid city: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by city: " + e.getMessage()));
        }
    }

    @GetMapping("/working-hours/{hours}")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersByWorkingHours(@PathVariable String hours) {
        try {
            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersByWorkingHours(hours);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved by working hours"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid working hours: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by working hours: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> searchTeamMembers(@RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Search query cannot be empty"));
            }
            List<TeamMemberDTO> teamMembers = teamMemberService.searchTeamMembers(q.trim());
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search team members: " + e.getMessage()));
        }
    }

    @GetMapping("/joined-between")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersJoinedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            if (start.isAfter(end)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Start date cannot be after end date"));
            }

            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersJoinedBetween(start, end);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved by join date range"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by join date range: " + e.getMessage()));
        }
    }

    @GetMapping("/age-range")
    public ResponseEntity<ApiResponse<List<TeamMemberDTO>>> getTeamMembersByAgeRange(
            @RequestParam int minAge,
            @RequestParam int maxAge) {
        try {
            if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid age range parameters"));
            }

            List<TeamMemberDTO> teamMembers = teamMemberService.getTeamMembersByAgeRange(minAge, maxAge);
            return ResponseEntity.ok(ApiResponse.success(teamMembers, "Team members retrieved by age range"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team members by age range: " + e.getMessage()));
        }
    }

    // NEW: Get team stats with member details
    @GetMapping("/team-stats/{teamId}")
    public ResponseEntity<ApiResponse<TeamMemberService.TeamStatsDTO>> getTeamStatsWithMembers(@PathVariable Long teamId) {
        try {
            TeamMemberService.TeamStatsDTO teamStats = teamMemberService.getTeamStatsWithMembers(teamId);
            return ResponseEntity.ok(ApiResponse.success(teamStats, "Team statistics retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team statistics: " + e.getMessage()));
        }
    }

    // NEW: Get all teams with their statistics
    @GetMapping("/all-teams-stats")
    public ResponseEntity<ApiResponse<List<TeamMemberService.TeamStatsDTO>>> getAllTeamsWithStats() {
        try {
            List<TeamMemberService.TeamStatsDTO> teamsStats = teamMemberService.getAllTeamsWithStats();
            return ResponseEntity.ok(ApiResponse.success(teamsStats, "All teams statistics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve teams statistics: " + e.getMessage()));
        }
    }

    @PostMapping("/member-create")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> createTeamMember(
            @Valid @RequestBody TeamMemberDTO teamMemberDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                String errorMessage = String.join(", ", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Validation failed: " + errorMessage));
            }

            TeamMemberDTO createdMember = teamMemberService.createTeamMember(teamMemberDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(createdMember, "Team member created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create team member: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> updateTeamMember(
            @PathVariable Long id,
            @Valid @RequestBody TeamMemberDTO teamMemberDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());
                String errorMessage = String.join(", ", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Validation failed: " + errorMessage));
            }

            return teamMemberService.updateTeamMember(id, teamMemberDTO)
                    .map(updatedMember -> ResponseEntity.ok(ApiResponse.success(updatedMember, "Team member updated successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team member: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/supervisor/{supervisorId}")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> updateSupervisor(
            @PathVariable Long id,
            @PathVariable Long supervisorId) {
        try {
            return teamMemberService.updateSupervisor(id, supervisorId)
                    .map(updatedMember -> ResponseEntity.ok(ApiResponse.success(updatedMember, "Supervisor updated successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update supervisor: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/team/{teamId}")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> updateTeam(
            @PathVariable Long id,
            @PathVariable Long teamId) {
        try {
            return teamMemberService.updateTeam(id, teamId)
                    .map(updatedMember -> ResponseEntity.ok(ApiResponse.success(updatedMember, "Team updated successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update team: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/remove-supervisor")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> removeSupervisor(@PathVariable Long id) {
        try {
            return teamMemberService.removeSupervisor(id)
                    .map(updatedMember -> ResponseEntity.ok(ApiResponse.success(updatedMember, "Supervisor removed successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove supervisor: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/remove-team")
    public ResponseEntity<ApiResponse<TeamMemberDTO>> removeTeam(@PathVariable Long id) {
        try {
            return teamMemberService.removeTeam(id)
                    .map(updatedMember -> ResponseEntity.ok(ApiResponse.success(updatedMember, "Team removed successfully")))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error("Team member not found with id: " + id)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove team: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeamMember(@PathVariable Long id) {
        try {
            if (teamMemberService.deleteTeamMember(id)) {
                return ResponseEntity.ok(ApiResponse.success(null, "Team member deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Team member not found with id: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete team member: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/team/{teamId}/count")
    public ResponseEntity<ApiResponse<Long>> getTeamMemberCountByTeam(@PathVariable Long teamId) {
        try {
            long count = teamMemberService.getTeamMemberCountByTeam(teamId);
            return ResponseEntity.ok(ApiResponse.success(count, "Team member count retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team member count: " + e.getMessage()));
        }
    }

    @GetMapping("/stats/supervisor/{supervisorId}/count")
    public ResponseEntity<ApiResponse<Long>> getTeamMemberCountBySupervisor(@PathVariable Long supervisorId) {
        try {
            long count = teamMemberService.getTeamMemberCountBySupervisor(supervisorId);
            return ResponseEntity.ok(ApiResponse.success(count, "Team member count by supervisor retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve team member count by supervisor: " + e.getMessage()));
        }
    }
}