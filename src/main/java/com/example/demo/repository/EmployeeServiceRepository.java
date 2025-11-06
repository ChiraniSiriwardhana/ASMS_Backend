package com.example.demo.repository;

import com.example.demo.model.EmployeeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeServiceRepository extends JpaRepository<EmployeeService, Long> {
    List<EmployeeService> findByEmployeeId(Long employeeId);
    List<EmployeeService> findByServiceId(Long serviceId);
    Optional<EmployeeService> findByEmployeeIdAndServiceId(Long employeeId, Long serviceId);
    boolean existsByEmployeeIdAndServiceId(Long employeeId, Long serviceId);
}

