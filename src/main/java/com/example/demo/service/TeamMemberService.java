package com.example.demo.service;

import com.example.demo.dto.TeamMemberDTO;
import com.example.demo.model.Team;
import com.example.demo.model.TeamMember;
import com.example.demo.model.User;
import com.example.demo.repository.TeamMemberRepository;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamMemberService {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<TeamMemberDTO> getAllTeamMembers() {
        return teamMemberRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<TeamMemberDTO> getTeamMemberById(Long id) {
        return teamMemberRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<TeamMemberDTO> getTeamMembersByTeamId(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeamMemberDTO createTeamMember(TeamMemberDTO teamMemberDTO) {
        // Validate age
        validateAge(teamMemberDTO.getBirthDate());

        // Validate working hours
        validateWorkingHours(teamMemberDTO.getWorkingHoursPerDay());

        // Check if NIC already exists
        if (teamMemberRepository.existsByNic(teamMemberDTO.getNic())) {
            throw new IllegalArgumentException("Team member with NIC " + teamMemberDTO.getNic() + " already exists");
        }

        // Validate team if provided
        Team team = null;
        if (teamMemberDTO.getTeamId() != null) {
            team = teamRepository.findById(teamMemberDTO.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamMemberDTO.getTeamId()));
        }

        // Validate supervisor if provided
        User supervisor = null;
        if (teamMemberDTO.getSupervisorId() != null) {
            supervisor = userRepository.findById(teamMemberDTO.getSupervisorId())
                    .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + teamMemberDTO.getSupervisorId()));
        }

        TeamMember teamMember = convertToEntity(teamMemberDTO, team, supervisor);
        TeamMember savedMember = teamMemberRepository.save(teamMember);
        return convertToDTO(savedMember);
    }

    public Optional<TeamMemberDTO> updateTeamMember(Long id, TeamMemberDTO teamMemberDTO) {
        return teamMemberRepository.findById(id)
                .map(existingMember -> {
                    // Check if NIC is being changed to an existing one
                    if (!existingMember.getNic().equals(teamMemberDTO.getNic()) &&
                            teamMemberRepository.existsByNicAndIdNot(teamMemberDTO.getNic(), id)) {
                        throw new IllegalArgumentException("Team member with NIC " + teamMemberDTO.getNic() + " already exists");
                    }

                    // Validate age
                    validateAge(teamMemberDTO.getBirthDate());

                    // Validate working hours
                    validateWorkingHours(teamMemberDTO.getWorkingHoursPerDay());

                    // Validate team if provided
                    Team team = null;
                    if (teamMemberDTO.getTeamId() != null) {
                        team = teamRepository.findById(teamMemberDTO.getTeamId())
                                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamMemberDTO.getTeamId()));
                    }

                    // Validate supervisor if provided
                    User supervisor = null;
                    if (teamMemberDTO.getSupervisorId() != null) {
                        supervisor = userRepository.findById(teamMemberDTO.getSupervisorId())
                                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + teamMemberDTO.getSupervisorId()));
                    }

                    TeamMember updatedMember = convertToEntity(teamMemberDTO, team, supervisor);
                    updatedMember.setId(id);
                    updatedMember.setCreatedAt(existingMember.getCreatedAt()); // Preserve creation timestamp

                    TeamMember savedMember = teamMemberRepository.save(updatedMember);
                    return convertToDTO(savedMember);
                });
    }

    public boolean deleteTeamMember(Long id) {
        if (teamMemberRepository.existsById(id)) {
            teamMemberRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TeamMemberDTO> getTeamMembersBySpecialization(String specialization) {
        TeamMember.Specialization spec = TeamMember.Specialization.valueOf(specialization.toUpperCase());
        return teamMemberRepository.findBySpecialization(spec)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersByCity(String city) {
        TeamMember.District district = TeamMember.District.valueOf(city.toUpperCase());
        return teamMemberRepository.findByCity(district)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersBySupervisor(Long supervisorId) {
        return teamMemberRepository.findBySupervisorId(supervisorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public long getTeamMemberCountByTeam(Long teamId) {
        return teamMemberRepository.countByTeamId(teamId);
    }

    public long getTeamMemberCountBySupervisor(Long supervisorId) {
        return teamMemberRepository.countBySupervisorId(supervisorId);
    }

    public Optional<TeamMemberDTO> updateSupervisor(Long teamMemberId, Long supervisorId) {
        return teamMemberRepository.findById(teamMemberId)
                .map(teamMember -> {
                    User supervisor = null;
                    if (supervisorId != null) {
                        supervisor = userRepository.findById(supervisorId)
                                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + supervisorId));
                    }
                    teamMember.setSupervisor(supervisor);
                    TeamMember updatedMember = teamMemberRepository.save(teamMember);
                    return convertToDTO(updatedMember);
                });
    }

    public Optional<TeamMemberDTO> removeSupervisor(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .map(teamMember -> {
                    teamMember.setSupervisor(null);
                    TeamMember updatedMember = teamMemberRepository.save(teamMember);
                    return convertToDTO(updatedMember);
                });
    }

    public Optional<TeamMemberDTO> updateTeam(Long teamMemberId, Long teamId) {
        return teamMemberRepository.findById(teamMemberId)
                .map(teamMember -> {
                    Team team = null;
                    if (teamId != null) {
                        team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));
                    }
                    teamMember.setTeam(team);
                    TeamMember updatedMember = teamMemberRepository.save(teamMember);
                    return convertToDTO(updatedMember);
                });
    }

    public Optional<TeamMemberDTO> removeTeam(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .map(teamMember -> {
                    teamMember.setTeam(null);
                    TeamMember updatedMember = teamMemberRepository.save(teamMember);
                    return convertToDTO(updatedMember);
                });
    }

    public List<TeamMemberDTO> getTeamMembersWithoutSupervisor() {
        return teamMemberRepository.findBySupervisorIsNull()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersWithoutTeam() {
        return teamMemberRepository.findAll()
                .stream()
                .filter(member -> member.getTeam() == null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> searchTeamMembers(String searchTerm) {
        return teamMemberRepository.searchTeamMembers(searchTerm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // UPDATED: Get team members by working hours (now using String)
    public List<TeamMemberDTO> getTeamMembersByWorkingHours(String workingHours) {
        validateWorkingHours(workingHours); // Validate the input
        return teamMemberRepository.findByWorkingHoursPerDay(workingHours)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersJoinedBetween(LocalDate startDate, LocalDate endDate) {
        return teamMemberRepository.findByJoinedDateBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersByAgeRange(int minAge, int maxAge) {
        return teamMemberRepository.findAll()
                .stream()
                .filter(member -> member.getAge() >= minAge && member.getAge() <= maxAge)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // NEW: Get team statistics with member details
    public TeamStatsDTO getTeamStatsWithMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with ID: " + teamId));

        List<TeamMember> members = teamMemberRepository.findByTeamIdWithTeamDetails(teamId);

        return TeamStatsDTO.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .specialization(team.getSpecialization().name())
                .totalMembers(members.size())
                .averageAge(calculateAverageAge(members))
                .totalWorkingHours(calculateTotalWorkingHours(members))
                .memberDetails(members.stream().map(this::convertToDTO).collect(Collectors.toList()))
                .build();
    }

    // NEW: Get all teams with their statistics
    public List<TeamStatsDTO> getAllTeamsWithStats() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(team -> getTeamStatsWithMembers(team.getId()))
                .collect(Collectors.toList());
    }

    private double calculateAverageAge(List<TeamMember> members) {
        if (members.isEmpty()) return 0;
        return members.stream()
                .mapToInt(TeamMember::getAge)
                .average()
                .orElse(0);
    }

    private int calculateTotalWorkingHours(List<TeamMember> members) {
        return members.stream()
                .mapToInt(member -> Integer.parseInt(member.getWorkingHoursPerDay()))
                .sum();
    }

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18 || age > 80) {
            throw new IllegalArgumentException("Employee must be between 18 and 80 years old");
        }
    }

    // NEW: Validate working hours
    private void validateWorkingHours(String workingHours) {
        if (workingHours == null || workingHours.trim().isEmpty()) {
            throw new IllegalArgumentException("Working hours are required");
        }

        // Validate that it's one of the allowed values
        List<String> allowedHours = List.of("4", "6", "8", "10", "12");
        if (!allowedHours.contains(workingHours.trim())) {
            throw new IllegalArgumentException("Working hours must be one of: 4, 6, 8, 10, 12");
        }
    }

    // UPDATED: Convert DTO to Entity - with team relationship
    private TeamMember convertToEntity(TeamMemberDTO dto, Team team, User supervisor) {
        TeamMember entity = new TeamMember();
        entity.setFullName(dto.getFullName().trim());
        entity.setNic(dto.getNic().trim());
        entity.setContactNo(dto.getContactNo());
        entity.setBirthDate(dto.getBirthDate());
        entity.setAddress(dto.getAddress().trim());
        entity.setCity(TeamMember.District.valueOf(dto.getCity().toUpperCase()));
        entity.setSpecialization(TeamMember.Specialization.valueOf(dto.getSpecialization().toUpperCase()));
        entity.setJoinedDate(dto.getJoinedDate());

        // Set working hours as String directly
        entity.setWorkingHoursPerDay(dto.getWorkingHoursPerDay());

        // Set team relationship
        entity.setTeam(team);
        entity.setSupervisor(supervisor);
        return entity;
    }

    // UPDATED: Convert Entity to DTO - with team relationship
    private TeamMemberDTO convertToDTO(TeamMember entity) {
        TeamMemberDTO dto = new TeamMemberDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setNic(entity.getNic());
        dto.setContactNo(entity.getContactNo());
        dto.setBirthDate(entity.getBirthDate());
        dto.setAge(entity.getAge());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity().name());
        dto.setSpecialization(entity.getSpecialization().name());
        dto.setJoinedDate(entity.getJoinedDate());

        // Get working hours as String directly
        dto.setWorkingHoursPerDay(entity.getWorkingHoursPerDay());

        // Set team information
        dto.setTeamId(entity.getTeamId());
        dto.setTeamName(entity.getTeamName());

        // Set supervisor information
        dto.setSupervisorId(entity.getSupervisorId());
        dto.setSupervisorName(entity.getSupervisorName());
        return dto;
    }

    // NEW: Team Stats DTO inner class
    public static class TeamStatsDTO {
        private Long teamId;
        private String teamName;
        private String specialization;
        private int totalMembers;
        private double averageAge;
        private int totalWorkingHours;
        private List<TeamMemberDTO> memberDetails;

        // Builder pattern
        public static TeamStatsDTOBuilder builder() {
            return new TeamStatsDTOBuilder();
        }

        // Getters and setters
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }

        public int getTotalMembers() { return totalMembers; }
        public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }

        public double getAverageAge() { return averageAge; }
        public void setAverageAge(double averageAge) { this.averageAge = averageAge; }

        public int getTotalWorkingHours() { return totalWorkingHours; }
        public void setTotalWorkingHours(int totalWorkingHours) { this.totalWorkingHours = totalWorkingHours; }

        public List<TeamMemberDTO> getMemberDetails() { return memberDetails; }
        public void setMemberDetails(List<TeamMemberDTO> memberDetails) { this.memberDetails = memberDetails; }

        public static class TeamStatsDTOBuilder {
            private Long teamId;
            private String teamName;
            private String specialization;
            private int totalMembers;
            private double averageAge;
            private int totalWorkingHours;
            private List<TeamMemberDTO> memberDetails;

            public TeamStatsDTOBuilder teamId(Long teamId) {
                this.teamId = teamId;
                return this;
            }

            public TeamStatsDTOBuilder teamName(String teamName) {
                this.teamName = teamName;
                return this;
            }

            public TeamStatsDTOBuilder specialization(String specialization) {
                this.specialization = specialization;
                return this;
            }

            public TeamStatsDTOBuilder totalMembers(int totalMembers) {
                this.totalMembers = totalMembers;
                return this;
            }

            public TeamStatsDTOBuilder averageAge(double averageAge) {
                this.averageAge = averageAge;
                return this;
            }

            public TeamStatsDTOBuilder totalWorkingHours(int totalWorkingHours) {
                this.totalWorkingHours = totalWorkingHours;
                return this;
            }

            public TeamStatsDTOBuilder memberDetails(List<TeamMemberDTO> memberDetails) {
                this.memberDetails = memberDetails;
                return this;
            }

            public TeamStatsDTO build() {
                TeamStatsDTO stats = new TeamStatsDTO();
                stats.setTeamId(this.teamId);
                stats.setTeamName(this.teamName);
                stats.setSpecialization(this.specialization);
                stats.setTotalMembers(this.totalMembers);
                stats.setAverageAge(this.averageAge);
                stats.setTotalWorkingHours(this.totalWorkingHours);
                stats.setMemberDetails(this.memberDetails);
                return stats;
            }
        }
    }
}