package com.example.demo.controller;

import com.example.demo.dto.TeamRequestDTO;
import com.example.demo.dto.TeamResponseDTO;
import com.example.demo.model.Team;
import com.example.demo.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee/teams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/all")
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        List<TeamResponseDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        TeamResponseDTO team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @PostMapping("/create")
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO teamRequest) {
        TeamResponseDTO createdTeam = teamService.createTeam(teamRequest);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequestDTO teamRequest) {
        TeamResponseDTO updatedTeam = teamService.updateTeam(id, teamRequest);
        return ResponseEntity.ok(updatedTeam);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsByEmployee(@PathVariable Long employeeId) {
        List<TeamResponseDTO> teams = teamService.getTeamsByEmployee(employeeId);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<TeamResponseDTO>> getTeamsBySpecialization(
            @PathVariable Team.Specialization specialization) {
        List<TeamResponseDTO> teams = teamService.getTeamsBySpecialization(specialization);
        return ResponseEntity.ok(teams);
    }
}