package com.example.demo.repository.projects;

import com.example.demo.model.projects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCustomerId(Long customerId);
    List<Project> findByAssignedEmployeeId(Long employeeId);
    Optional<Project> findByIdAndCustomerId(Long id, Long customerId);
}