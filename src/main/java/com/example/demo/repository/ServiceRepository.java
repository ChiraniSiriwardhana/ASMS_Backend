package com.example.demo.repository;

import com.example.demo.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByIsActive(Boolean isActive);
    
    List<Service> findByCategory(String category);
    
    List<Service> findByServiceNameContainingIgnoreCase(String serviceName);
}
