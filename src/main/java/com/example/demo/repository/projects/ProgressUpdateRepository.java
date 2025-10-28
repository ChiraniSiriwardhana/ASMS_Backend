package com.example.demo.repository.projects;

import com.example.demo.model.projects.ProgressUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressUpdateRepository extends JpaRepository<ProgressUpdate, Long> {
    List<ProgressUpdate> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}