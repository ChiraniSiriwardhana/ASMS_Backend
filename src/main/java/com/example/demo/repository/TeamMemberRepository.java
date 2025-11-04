package com.example.demo.repository;

import com.example.demo.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // === Basic CRUD & Validation ===
    boolean existsByNic(String nic);
    boolean existsByNicAndIdNot(String nic, Long id);

    // === Team-based Queries ===
    List<TeamMember> findByTeamId(String teamId);
    long countByTeamId(String teamId);

    // === Supervisor Relationship Queries ===
    // Find all team members under a specific supervisor
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId ORDER BY tm.fullName")
    List<TeamMember> findBySupervisorId(@Param("supervisorId") Long supervisorId);

    // Count team members under a specific supervisor
    @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId")
    long countBySupervisorId(@Param("supervisorId") Long supervisorId);

    // Find team members without a supervisor assigned
    List<TeamMember> findBySupervisorIsNull();

    // Check if supervisor has any team members
    @Query("SELECT CASE WHEN COUNT(tm) > 0 THEN true ELSE false END FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId")
    boolean existsBySupervisorId(@Param("supervisorId") Long supervisorId);

    // === Supervisor with Additional Filters ===
    // Find active team members under a supervisor
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.supervisor.isActive = true")
    List<TeamMember> findBySupervisorIdAndSupervisorActive(@Param("supervisorId") Long supervisorId);

    // Find team members by supervisor and team
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.teamId = :teamId")
    List<TeamMember> findBySupervisorIdAndTeamId(@Param("supervisorId") Long supervisorId, @Param("teamId") String teamId);

    // Count team members by supervisor and specialization
    @Query("SELECT COUNT(tm) FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.specialization = :specialization")
    long countBySupervisorIdAndSpecialization(@Param("supervisorId") Long supervisorId,
                                              @Param("specialization") TeamMember.Specialization specialization);

    // === Advanced Supervisor Queries ===
    // Find supervisors with their team member count
    @Query("SELECT tm.supervisor.id, COUNT(tm) FROM TeamMember tm GROUP BY tm.supervisor.id HAVING tm.supervisor.id IS NOT NULL")
    List<Object[]> findSupervisorTeamCounts();

    // Find supervisors with team member count greater than specified
    @Query("SELECT tm.supervisor.id, COUNT(tm) FROM TeamMember tm GROUP BY tm.supervisor.id HAVING COUNT(tm) > :minCount AND tm.supervisor.id IS NOT NULL")
    List<Object[]> findSupervisorsWithTeamSizeGreaterThan(@Param("minCount") long minCount);

    // Find available supervisors (those with less than max team members)
    @Query("SELECT u.id FROM User u WHERE u.role = 'SUPERVISOR' AND u.isActive = true AND " +
            "(SELECT COUNT(tm) FROM TeamMember tm WHERE tm.supervisor.id = u.id) < :maxTeamSize")
    List<Long> findAvailableSupervisors(@Param("maxTeamSize") int maxTeamSize);

    // === Specialization & Skills ===
    List<TeamMember> findBySpecialization(TeamMember.Specialization specialization);

    // Find team members by supervisor and specialization
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.specialization = :specialization")
    List<TeamMember> findBySupervisorIdAndSpecialization(@Param("supervisorId") Long supervisorId,
                                                         @Param("specialization") TeamMember.Specialization specialization);

    // === Location-based ===
    List<TeamMember> findByCity(TeamMember.District city);

    // Find team members by supervisor and city
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.city = :city")
    List<TeamMember> findBySupervisorIdAndCity(@Param("supervisorId") Long supervisorId,
                                               @Param("city") TeamMember.District city);

    // === Work Schedule ===
    List<TeamMember> findByWorkingHoursPerDay(TeamMember.WorkingHours workingHours);

    // Find team members by supervisor and working hours
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.workingHoursPerDay = :workingHours")
    List<TeamMember> findBySupervisorIdAndWorkingHours(@Param("supervisorId") Long supervisorId,
                                                       @Param("workingHours") TeamMember.WorkingHours workingHours);

    // === Date-based Queries ===
    List<TeamMember> findByJoinedDateBetween(LocalDate startDate, LocalDate endDate);

    // Find team members who joined under a supervisor within date range
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND tm.joinedDate BETWEEN :startDate AND :endDate")
    List<TeamMember> findBySupervisorIdAndJoinedDateBetween(@Param("supervisorId") Long supervisorId,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    // === Search & Filter ===
    @Query("SELECT tm FROM TeamMember tm WHERE " +
            "LOWER(tm.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.nic) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.contactNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.teamId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TeamMember> searchTeamMembers(@Param("searchTerm") String searchTerm);

    // Search team members under a specific supervisor
    @Query("SELECT tm FROM TeamMember tm WHERE tm.supervisor.id = :supervisorId AND " +
            "(LOWER(tm.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.nic) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.contactNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(tm.teamId) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<TeamMember> searchTeamMembersBySupervisor(@Param("supervisorId") Long supervisorId,
                                                   @Param("searchTerm") String searchTerm);

    // === Combined Filters ===
    @Query("SELECT tm FROM TeamMember tm WHERE " +
            "(:teamId IS NULL OR tm.teamId = :teamId) AND " +
            "(:specialization IS NULL OR tm.specialization = :specialization) AND " +
            "(:city IS NULL OR tm.city = :city) AND " +
            "(:supervisorId IS NULL OR tm.supervisor.id = :supervisorId)")
    List<TeamMember> findByTeamIdAndSpecializationAndCityAndSupervisorId(
            @Param("teamId") String teamId,
            @Param("specialization") TeamMember.Specialization specialization,
            @Param("city") TeamMember.District city,
            @Param("supervisorId") Long supervisorId);

    // === Performance Optimized Queries ===
    // Eager loading of supervisor details
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.supervisor WHERE tm.supervisor.id = :supervisorId")
    List<TeamMember> findBySupervisorIdWithSupervisorDetails(@Param("supervisorId") Long supervisorId);

    // Find all team members with supervisor details (for reporting)
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.supervisor WHERE tm.supervisor IS NOT NULL")
    List<TeamMember> findAllWithSupervisorDetails();
}