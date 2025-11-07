package com.example.demo.repository;

import com.example.demo.model.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByIsActiveTrueOrderByCreatedAtDesc();
    List<FAQ> findAllByOrderByCreatedAtDesc();
    List<FAQ> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(String category);
}
