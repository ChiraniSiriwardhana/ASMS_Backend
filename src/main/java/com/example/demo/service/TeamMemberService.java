package com.example.demo.service;

import com.example.demo.dto.TeamMemberDTO;
import com.example.demo.model.TeamMember;
import com.example.demo.model.User;
import com.example.demo.repository.TeamMemberRepository;
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

    public List<TeamMemberDTO> getTeamMembersByTeamId(String teamId) {
        return teamMemberRepository.findByTeamId(teamId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeamMemberDTO createTeamMember(TeamMemberDTO teamMemberDTO) {
        // Validate age
        validateAge(teamMemberDTO.getBirthDate());

        // Check if NIC already exists
        if (teamMemberRepository.existsByNic(teamMemberDTO.getNic())) {
            throw new IllegalArgumentException("Team member with NIC " + teamMemberDTO.getNic() + " already exists");
        }

        // Validate supervisor if provided
        User supervisor = null;
        if (teamMemberDTO.getSupervisorId() != null) {
            supervisor = userRepository.findById(teamMemberDTO.getSupervisorId())
                    .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + teamMemberDTO.getSupervisorId()));
        }

        TeamMember teamMember = convertToEntity(teamMemberDTO, supervisor);
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

                    // Validate supervisor if provided
                    User supervisor = null;
                    if (teamMemberDTO.getSupervisorId() != null) {
                        supervisor = userRepository.findById(teamMemberDTO.getSupervisorId())
                                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found with ID: " + teamMemberDTO.getSupervisorId()));
                    }

                    TeamMember updatedMember = convertToEntity(teamMemberDTO, supervisor);
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

    public long getTeamMemberCountByTeam(String teamId) {
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

    public List<TeamMemberDTO> getTeamMembersWithoutSupervisor() {
        return teamMemberRepository.findBySupervisorIsNull()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> searchTeamMembers(String searchTerm) {
        return teamMemberRepository.searchTeamMembers(searchTerm)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TeamMemberDTO> getTeamMembersByWorkingHours(String workingHours) {
        TeamMember.WorkingHours hours = TeamMember.WorkingHours.fromString(workingHours);
        return teamMemberRepository.findByWorkingHoursPerDay(hours)
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

    private void validateAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18 || age > 80) {
            throw new IllegalArgumentException("Employee must be between 18 and 80 years old");
        }
    }

    private TeamMember convertToEntity(TeamMemberDTO dto, User supervisor) {
        TeamMember entity = new TeamMember();
        entity.setFullName(dto.getFullName().trim());
        entity.setNic(dto.getNic().trim());
        entity.setContactNo(dto.getContactNo());
        entity.setBirthDate(dto.getBirthDate());
        entity.setAddress(dto.getAddress().trim());
        entity.setCity(TeamMember.District.valueOf(dto.getCity().toUpperCase()));
        entity.setSpecialization(TeamMember.Specialization.valueOf(dto.getSpecialization().toUpperCase()));
        entity.setJoinedDate(dto.getJoinedDate());
        entity.setWorkingHoursPerDay(TeamMember.WorkingHours.fromString(dto.getWorkingHoursPerDay()));
        entity.setTeamId(dto.getTeamId());
        entity.setSupervisor(supervisor);
        return entity;
    }

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
        dto.setWorkingHoursPerDay(entity.getWorkingHoursPerDay().getHours());
        dto.setTeamId(entity.getTeamId());
        dto.setSupervisorId(entity.getSupervisorId());
        dto.setSupervisorName(entity.getSupervisorName());
        return dto;
    }
}