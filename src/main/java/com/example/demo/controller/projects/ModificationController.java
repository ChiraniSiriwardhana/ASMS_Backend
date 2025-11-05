package com.example.demo.controller.projects;

import com.example.demo.model.projects.ModificationRequest;
import com.example.demo.service.projects.ModificationRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.projects.ModificationRequestDto;
import com.example.demo.enums.ModificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api/projects/modifications")
@RequiredArgsConstructor
public class ModificationController {

    private final ModificationRequestService modificationRequestService;

    @PostMapping
    public ResponseEntity<ModificationRequest> createModificationRequest(
            @Valid @RequestBody ModificationRequestDto request) {
        ModificationRequest response = modificationRequestService.createModificationRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ModificationRequest>> getCustomerModificationRequests(
            @PathVariable Long customerId) {
        List<ModificationRequest> requests = modificationRequestService.getModificationRequestsByCustomerId(customerId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ModificationRequest>> getPendingModificationRequests() {
        List<ModificationRequest> requests = modificationRequestService.getPendingModificationRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ModificationRequest> updateModificationRequestStatus(
            @PathVariable Long id,
            @RequestParam ModificationStatus status) {
        ModificationRequest request = modificationRequestService.updateModificationRequestStatus(id, status);
        return ResponseEntity.ok(request);
    }
}