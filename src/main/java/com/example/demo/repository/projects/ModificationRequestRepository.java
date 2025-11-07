package com.example.demo.repository.projects;

import com.example.demo.model.projects.ModificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ModificationRequestRepository extends JpaRepository<ModificationRequest, Long> {
    List<ModificationRequest> findByCustomerId(Long customerId);
}