package com.example.demo.repository;

import com.example.demo.model.Team;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByEmployee(User employee);
    List<Team> findByEmployeeId(Long employeeId);
    List<Team> findBySpecialization(Team.Specialization specialization);
    Optional<Team> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT t FROM Team t WHERE t.employee.id = :employeeId")
    List<Team> findTeamsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(t) FROM Team t WHERE t.employee.id = :employeeId")
    Long countByEmployeeId(@Param("employeeId") Long employeeId);
}